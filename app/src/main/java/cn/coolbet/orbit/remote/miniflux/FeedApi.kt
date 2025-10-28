package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.model.OrderPublishedAt
import cn.coolbet.orbit.model.domain.Feed
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FeedApi @Inject constructor(
    private val api: MiniFeedApi
) {
    suspend fun getFeeds(): List<Feed> = withContext(Dispatchers.IO) {
        api.getFeeds().map { it.to() }
    }
}

interface MiniFeedApi {
    @GET("v1/feeds")
    suspend fun getFeeds() : List<FeedResponse>
}

fun FeedResponse.to(): Feed {
    return Feed(
        id = this.id,
        userId = this.userId,
        feedURL = this.feedUrl,
        siteURL = this.siteUrl,
        title = this.title,
        errorCount = this.parsingErrorCount,
        errorMsg = this.parsingErrorMessage ?: "",
        folderId = this.category?.id ?: 0L,
        desc = "",
        hideGlobally = this.hideGlobally,
        onlyShowUnread = false,
        order = OrderPublishedAt,
        iconURL = this.icon?.let { "v1/icons/${it.iconId}" } ?: "",
    )
}

data class FeedResponse(
    val id: Long,

    @SerializedName("user_id")
    val userId: Long,

    @SerializedName("feed_url")
    val feedUrl: String,

    @SerializedName("site_url")
    val siteUrl: String,

    val title: String,
    val description: String?,

    @SerializedName("checked_at")
    val checkedAt: String?,

    @SerializedName("next_check_at")
    val nextCheckAt: String?,

    @SerializedName("etag_header")
    val etagHeader: String?,

    @SerializedName("last_modified_header")
    val lastModifiedHeader: String?,

    @SerializedName("parsing_error_message")
    val parsingErrorMessage: String?,

    @SerializedName("parsing_error_count")
    val parsingErrorCount: Int,

    @SerializedName("hide_globally")
    val hideGlobally: Boolean,

    val category: CategoryResponse?,
    val icon: IconResponse?
)

data class IconResponse(
    @SerializedName("feed_id")
    val feedId: Long,

    @SerializedName("icon_id")
    val iconId: Long,

    @SerializedName("external_icon_id")
    val externalIconId: String
)
