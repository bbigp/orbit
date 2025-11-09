package cn.coolbet.orbit.remote.miniflux

import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
import cn.coolbet.orbit.model.domain.Media
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

interface MiniEntryApi {

    @GET("v1/entries")
    suspend fun getEntries(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("status") statuses: List<String>,
        @Query("order") order: String,
        @Query("direction") direction: String,
        @Query("globally_visible") globallyVisible: Boolean,
    ): EntriesPageResponse
}


fun EntryResponse.to(): Entry {
    return Entry(
        id = id, userId = userId, feedId = feedId, status = EntryStatus.from(status),
        hash = hash, title = title, url = url, publishedAt = publishedAt?.time ?: 0, content = content,
        author = author, starred = starred, readingTime = readingTime,
        createdAt = createdAt?.time ?: 0, changedAt = changedAt?.time ?: 0, tags = tags ?: emptyList(),
        feed = feed.to(), medias = enclosures?.map { it.to() } ?: emptyList()
    )
}

data class EntriesPageResponse(
    @SerializedName("total") val total: Int = 0,
    @SerializedName("entries") val entries: List<EntryResponse> = emptyList(),
)

data class EntryResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("feed_id") val feedId: Long = 0,
    @SerializedName("status") val status: String = "unread",
    @SerializedName("hash") val hash: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("url") val url: String = "",
    @SerializedName("published_at") val publishedAt: Date? = null,
    @SerializedName("created_at") val createdAt: Date? = null,
    @SerializedName("changed_at") val changedAt: Date? = null,
    @SerializedName("content") val content: String = "",
    @SerializedName("author") val author: String = "",
    @SerializedName("starred") val starred: Boolean = false,
    @SerializedName("reading_time") val readingTime: Int = 0,
    @SerializedName("enclosures") val enclosures: List<EnclosureResponse>? = emptyList(),
    @SerializedName("feed") val feed: FeedResponse = FeedResponse.EMPTY,
    @SerializedName("tags") val tags: List<String>? = emptyList(),
)

data class EnclosureResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("entry_id") val entryId: Long,
    @SerializedName("url") val url: String = "",
    @SerializedName("mime_type") val mimeType: String = "",
    @SerializedName("size") val size: Int = 0,
)

fun EnclosureResponse.to(): Media {
    return Media(
        id = id, userId = userId, entryId = entryId,
        url = url, mimeType = mimeType, size = size,
    )
}