package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.model.OrderPublishedAt
import cn.coolbet.orbit.model.domain.Feed
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton


@Singleton
class FeedApi @Inject constructor(
    private val apiProvider: Provider<MiniFeedApi>
) {
    suspend fun getFeeds(): List<Feed> = withContext(Dispatchers.IO) {
        apiProvider.get().getFeeds().map { it.to() }
    }
}