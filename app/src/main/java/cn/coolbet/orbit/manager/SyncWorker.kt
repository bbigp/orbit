package cn.coolbet.orbit.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import cn.coolbet.orbit.R
import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.dao.FeedDao
import cn.coolbet.orbit.dao.FolderDao
import cn.coolbet.orbit.dao.SyncTaskRecordDao
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import cn.coolbet.orbit.remote.EntryApi
import cn.coolbet.orbit.remote.FolderApi
import cn.coolbet.orbit.remote.miniflux.FeedApi


const val IGNORE_TIME_KEY = "ignore_last_sync_time"
const val FULL_RESYNC_KEY = "full_resync"

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val dao: SyncTaskRecordDao,
    private val entryApi: EntryApi,
    private val feedApi: FeedApi,
    private val folderApi: FolderApi,
    private val preference: Preference,
    private val feedDao: FeedDao,
    private val folderDao: FolderDao,
    private val entryDao: EntryDao,
    private val eventBus: EventBus,
) : CoroutineWorker(appContext, workerParams) {

    private val NOTIFICATION_ID = 101
    private val CHANNEL_ID = "sync_channel"
    private val CHANNEL_NAME = "数据同步"

    override suspend fun getForegroundInfo(): ForegroundInfo {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("同步中") // 通知标题
            .setContentText("正在后台同步您的数据...") // 通知内容
            // 🌟 必须设置一个小图标，否则通知不会显示
            .setSmallIcon(R.drawable.loading) // 替换为您的实际图标资源
            .setOngoing(true) // 设置为持续通知，表示工作正在进行
            .setCategory(Notification.CATEGORY_SERVICE) // 类别设置为服务
            .build()
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    override suspend fun doWork(): Result {
//        val ignoreLastSyncTime = inputData.getBoolean(IGNORE_TIME_KEY, false)
        this.startTaskFeedsThenEntries()
        // 如果同步失败，可以选择重试或直接失败
        // 失败后重试 (Result.retry())
        // 彻底失败 (Result.failure())
        return Result.success()
    }

    /**
     * 创建通知渠道 (仅在 API 26 及以上版本需要)。
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // 前台服务通常使用 IMPORTANCE_LOW
            ).apply {
                description = "用于显示后台数据同步状态"
                // 设置为静音，因为是持续性通知，避免打扰用户
                setSound(null, null)
            }

            // 将渠道注册到系统
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    suspend fun startTask(fullResync: Boolean = false) {
        val user = preference.userProfile()
        if (user.isEmpty) {
            Log.i("sync", "未登录，无法同步")
            return
        }
        val userId = user.id
        val now = System.currentTimeMillis()
        val record = dao.getLastRecord(userId)
        var lastSyncProgress =
            record?.toTime ?: (now - DateUtils.DAY_IN_MILLIS * 365)

        lastSyncProgress = now - DateUtils.DAY_IN_MILLIS * 365

        val taskId = dao.insert(SyncTaskRecord(
            executeTime = now, userId = userId, status = SyncTaskRecord.RUNNING
        ))
        Log.i("sync", "开始同步 $lastSyncProgress $taskId")
        val size = 25
        var page = 1

        var from = 0L
        var to = 0L

        var entryCount = 0
        var mediaCount = 0
        var feedCount = 0
        var folderCount = 0

        val seenFeedIds = mutableSetOf<Long>()
        val seenFolderIds = mutableSetOf<Long>()
        var status = SyncTaskRecord.OK
        var errorMsg = ""
        try {
            while (true) {
                val listInfo = entryApi.getEntries(page = page, size = size)
                if (listInfo.isEmpty()) {
                    break
                }
                if (page == 1) {
                    to = listInfo.firstOrNull()?.changedAt ?: 0
                }

                val feeds = mutableListOf<Feed>()
                val folders = mutableListOf<Folder>()

                for (e in listInfo) {
                    if (e.feedId !in seenFeedIds) {
                        feeds.add(e.feed)
                        seenFeedIds.add(e.feedId)
                    }
                    if (e.feed.folder.id !in seenFolderIds) {
                        folders.add(e.feed.folder)
                        seenFolderIds.add(e.feed.folderId)
                    }
                    mediaCount += e.medias.size
                }
                entryDao.batchSave(listInfo)
                feedDao.batchSave(feeds)
                folderDao.batchSave(folders)

                entryCount += listInfo.size
                feedCount += feeds.size
                folderCount += folders.size

                feeds.clear()
                folders.clear()

                from = listInfo.last().changedAt

                val hasMore = listInfo.size >= size
                val reachedLastProgress = from < lastSyncProgress
                if (!hasMore || reachedLastProgress) {
                    break
                }
                if (page > 10) break
                page++
            }
        } catch (e: Exception) {
            status = SyncTaskRecord.FAIL
            errorMsg = e.message ?: ""
        } finally {
            val row = dao.updateFinish(
                entry = entryCount, media = mediaCount, feed = feedCount, folder = folderCount,
                fromTime = from, toTime = to, status = status, errorMsg = errorMsg,
                id = taskId
            )
            Log.i("sync", "执行完毕: $row $taskId $status $errorMsg")
            val result = eventBus.post(Evt.CacheInvalidated(userId))
            Log.i("eventbus", "缓存失效事件发送 $result")
        }
    }

    suspend fun startTaskFeedsThenEntries(fullResync: Boolean = false) {
        val user = preference.userProfile()
        if (user.isEmpty) {
            Log.i("sync", "未登录，无法同步")
            return
        }

        val userId = user.id
        val now = System.currentTimeMillis()
        val record = dao.getLastRecord(userId)
        var lastSyncProgress = record?.toTime ?: (now - DateUtils.DAY_IN_MILLIS * 365)
        if (fullResync) {
            lastSyncProgress = now - DateUtils.DAY_IN_MILLIS * 365
        }
        val taskId = dao.insert(
            SyncTaskRecord(
                executeTime = now,
                userId = userId,
                status = SyncTaskRecord.RUNNING
            )
        )

        var from = 0L
        var to = 0L
        var entryCount = 0
        var mediaCount = 0
        var feedCount = 0
        var folderCount = 0
        var status = SyncTaskRecord.OK
        var errorMsg = ""

        try {
            // Phase 1: full feeds/folders sync
            val remoteFolders = folderApi.getFolders()
            val remoteFeeds = feedApi.getFeeds()
            folderDao.batchSave(remoteFolders)
            feedDao.batchSave(remoteFeeds)
            feedCount = remoteFeeds.size
            folderCount = remoteFolders.size

            // Phase 2: full entries sync
            val size = 100
            var page = 1
            while (true) {
                val listInfo = entryApi.getEntries(page = page, size = size)
                if (listInfo.isEmpty()) break
                if (page == 1) {
                    to = listInfo.firstOrNull()?.changedAt ?: 0L
                }

                entryDao.batchSave(listInfo)
                entryCount += listInfo.size
                mediaCount += listInfo.sumOf { it.medias.size }
                from = listInfo.last().changedAt

                val hasMore = listInfo.size >= size
                val reachedLastProgress = from < lastSyncProgress
                if (!hasMore || reachedLastProgress) break
                page++
            }
        } catch (e: Exception) {
            status = SyncTaskRecord.FAIL
            errorMsg = e.message ?: ""
        } finally {
            dao.updateFinish(
                entry = entryCount,
                media = mediaCount,
                feed = feedCount,
                folder = folderCount,
                fromTime = from,
                toTime = to,
                status = status,
                errorMsg = errorMsg,
                id = taskId
            )
            val result = eventBus.post(Evt.CacheInvalidated(userId))
            Log.i("eventbus", "缓存失效事件发送 $result")
        }
    }

}
