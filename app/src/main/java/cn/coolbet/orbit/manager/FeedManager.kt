package cn.coolbet.orbit.manager

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
        val feeds = feedApi.getFeeds()
        feedDao.batchSave(feeds)

        val entries = entryApi.getEntries(
            page = 1,
            size = 100,
            feedId = feedId,
        )
        entryDao.batchSave(entries)
        eventBus.post(Evt.CacheInvalidated(session.user.id))
    }
}
