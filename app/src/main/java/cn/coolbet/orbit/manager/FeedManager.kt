package cn.coolbet.orbit.manager

import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.dao.FeedDao
import cn.coolbet.orbit.dao.MediaDao
import cn.coolbet.orbit.remote.EntryApi
import cn.coolbet.orbit.remote.miniflux.FeedApi
import cn.coolbet.orbit.remote.miniflux.FeedCreationRequest
import cn.coolbet.orbit.remote.miniflux.FeedModificationRequest

private const val SYNC_WORK_TAG = "data_sync_on_home_entry_tag"

class FeedManager(
    private val feedApi: FeedApi,
    private val entryApi: EntryApi,
    private val session: Session,
    private val workManager: WorkManager,
    private val feedDao: FeedDao,
    private val entryDao: EntryDao,
    private val mediaDao: MediaDao,
    private val eventBus: EventBus,
) {

    suspend fun subscribeFeed(feedUrl: String, folderId: Long): Long {
        val normalized = feedUrl.trim()
        require(normalized.isNotBlank()) { "Feed URL is empty" }
        check(!session.user.isEmpty) { "Not logged in" }
        require(folderId > 0L) { "Folder id is invalid" }

        val feedId = feedApi.createFeed(
            FeedCreationRequest(
                feedUrl = normalized,
                categoryId = folderId,
            )
        )
        check(feedId > 0L) { "Create feed failed" }

        syncNewSubscription(feedId)
        triggerSync()
        return feedId
    }

    suspend fun unsubscribeFeed(feedId: Long) {
        check(!session.user.isEmpty) { "Not logged in" }
        require(feedId > 0L) { "Feed id is invalid" }

        feedApi.deleteFeed(feedId)
        mediaDao.deleteByFeedId(feedId)
        entryDao.deleteByFeedId(feedId)
        feedDao.deleteById(feedId)
        eventBus.post(Evt.CacheInvalidated(session.user.id))
    }

    suspend fun updateFeed(feedId: Long, title: String?, folderId: Long?) {
        check(!session.user.isEmpty) { "Not logged in" }
        require(feedId > 0L) { "Feed id is invalid" }
        require(title != null || folderId != null) { "No feed changes" }

        val updatedFeed = feedApi.updateFeed(
            feedId = feedId,
            request = FeedModificationRequest(
                title = title,
                categoryId = folderId,
            )
        )
        feedDao.batchSave(listOf(updatedFeed))
        eventBus.post(Evt.CacheInvalidated(session.user.id))
    }

    private fun triggerSync() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag(SYNC_WORK_TAG)
            .build()
        workManager.beginUniqueWork(
            SYNC_WORK_TAG,
            ExistingWorkPolicy.KEEP,
            syncRequest
        ).enqueue()
    }

    private suspend fun syncNewSubscription(feedId: Long) {
        Log.i("syncNewSubscription", "$feedId")
        val entries = entryApi.getEntries(
            page = 1,
            size = 30,
            feedId = feedId,
        )
        val feeds = entries
            .map { it.feed }
            .filter { it.id > 0L }
            .distinctBy { it.id }
        if (feeds.isNotEmpty()) {
            feedDao.batchSave(feeds)
        }
        Log.i("syncNewSubscription", "${entries.size}")
        entryDao.batchSave(entries)
        eventBus.post(Evt.CacheInvalidated(session.user.id))
    }
}
