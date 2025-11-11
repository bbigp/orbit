package cn.coolbet.orbit.manager

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cn.coolbet.orbit.dao.FeedMapper
import cn.coolbet.orbit.dao.FolderMapper
import cn.coolbet.orbit.dao.SyncTaskRecordDao
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.domain.Media
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import cn.coolbet.orbit.remote.EntryApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


const val IGNORE_TIME_KEY = "ignore_last_sync_time"
const val FULL_RESYNC_KEY = "full_resync"

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: SyncTaskRecordDao,
    private val entryApi: EntryApi,
    private val preference: Preference,
    private val feedMapper: FeedMapper,
    private val folderMapper: FolderMapper,
    private val eventBus: EventBus,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
//        val ignoreLastSyncTime = inputData.getBoolean(IGNORE_TIME_KEY, false)
        this.startTask()
        // 如果同步失败，可以选择重试或直接失败
        // 失败后重试 (Result.retry())
        // 彻底失败 (Result.failure())
        return Result.success()
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

                val medias = mutableListOf<Media>()
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
                    medias.addAll(e.medias)
                }
                //entry save
                //media save
                feedMapper.batchSave(feeds)
                folderMapper.batchSave(folders)

                entryCount += listInfo.size
                mediaCount += medias.size
                feedCount += feeds.size
                folderCount += folders.size

                medias.clear()
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
            eventBus.post(Evt.CacheInvalidated(userId))
        }
    }

}