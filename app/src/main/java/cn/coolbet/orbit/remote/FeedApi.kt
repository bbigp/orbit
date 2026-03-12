package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.model.domain.Feed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FeedApi(
    private val miniFeedApi: MiniFeedApi
) {
    suspend fun getFeeds(): List<Feed> = withContext(Dispatchers.IO) {
        minifluxRequest {
            miniFeedApi.getFeeds().map { it.to() }
        }
    }

    suspend fun createFeed(request: FeedCreationRequest): Long = withContext(Dispatchers.IO) {
        minifluxRequest {
            miniFeedApi.createFeed(request).feedId
        }
    }

    suspend fun updateFeed(feedId: Long, request: FeedModificationRequest): Feed = withContext(Dispatchers.IO) {
        minifluxRequest {
            miniFeedApi.updateFeed(feedId, request).to()
        }
    }

    suspend fun deleteFeed(feedId: Long) = withContext(Dispatchers.IO) {
        minifluxRequest {
            miniFeedApi.deleteFeed(feedId)
        }
    }
}
