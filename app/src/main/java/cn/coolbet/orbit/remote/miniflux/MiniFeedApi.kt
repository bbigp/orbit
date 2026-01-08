package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.model.OrderPublishedAt
import cn.coolbet.orbit.model.domain.Feed
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

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
        errorMsg = this.parsingErrorMessage,
        folderId = this.category.id,
        desc = "",
        iconURL = if(this.icon.isEmpty) "" else this.icon.let { "v1/icons/${it.iconId}" },
        folder = this.category.to()
    )
}

data class FeedResponse(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("feed_url") val feedUrl: String = "",
    @SerializedName("site_url") val siteUrl: String = "",
    val title: String = "",
    val description: String = "",
    @SerializedName("checked_at") val checkedAt: String? = null,
    @SerializedName("next_check_at") val nextCheckAt: String? = null,
    @SerializedName("etag_header") val etagHeader: String = "",
    @SerializedName("last_modified_header") val lastModifiedHeader: String = "",
    @SerializedName("parsing_error_message") val parsingErrorMessage: String = "",
    @SerializedName("parsing_error_count") val parsingErrorCount: Int = 0,
    @SerializedName("hide_globally") val hideGlobally: Boolean = false,
    val category: CategoryResponse = CategoryResponse.EMPTY,
    val icon: IconResponse = IconResponse.EMPTY,
) {
    companion object {
        val EMPTY = FeedResponse(id = 0, userId = 0)
    }
}

data class IconResponse(
    @SerializedName("feed_id") val feedId: Long,
    @SerializedName("icon_id") val iconId: Long,
    @SerializedName("external_icon_id") val externalIconId: String = "",
) {
    companion object {
        val EMPTY = IconResponse(feedId = 0, iconId = 0)
    }
    val isEmpty: Boolean get() = iconId == 0L
}
