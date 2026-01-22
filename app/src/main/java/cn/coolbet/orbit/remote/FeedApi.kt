package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.model.domain.Feed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FeedApi(
    private val miniFeedApi: MiniFeedApi
) {
    suspend fun getFeeds(): List<Feed> = withContext(Dispatchers.IO) {
        miniFeedApi.getFeeds().map { it.to() }
    }
}