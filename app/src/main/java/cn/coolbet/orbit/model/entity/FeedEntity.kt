package cn.coolbet.orbit.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.coolbet.orbit.model.OrderPublishedAt
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.remote.miniflux.FeedResponse

@Entity(tableName = "feeds")
data class FeedEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "user_id") val userId: Long = 0,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "feed_url") val feedURL: String = "",
    @ColumnInfo(name = "site_url", defaultValue = "") val siteURL: String = "",
    @ColumnInfo(name = "icon_url", defaultValue = "") val iconURL: String = "",
    @ColumnInfo(name = "error_count", defaultValue = "0") val errorCount: Int = 0,
    @ColumnInfo(name = "error_msg", defaultValue = "") val errorMsg: String = "",
    @ColumnInfo(name = "folder_id", defaultValue = "0") val folderId: Long = 0,
    @ColumnInfo(name = "hide_globally", defaultValue = "false") val hideGlobally: Boolean = false,

    @ColumnInfo(name = "only_show_unread", defaultValue = "false") val onlyShowUnread: Boolean = false,
    @ColumnInfo(name = "orderx", defaultValue = "publishedAt") val orderx: String = OrderPublishedAt,
) {
    companion object {
        fun fromResponse(response: FeedResponse): FeedEntity {
            return FeedEntity(
                id = response.id,
                userId = response.userId,
                title = response.title,
                feedURL = response.feedUrl,
                siteURL = response.siteUrl,
                iconURL = response.icon?.let { "v1/icons/${it.iconId}" } ?: "",
                errorCount = response.parsingErrorCount,
                errorMsg = response.parsingErrorMessage ?: "",
                folderId = response.category?.id ?: 0,
                hideGlobally = response.hideGlobally,
            )
        }
    }
}

fun Feed.toEntity(): FeedEntity {
    return FeedEntity(
        id = this.id,
        userId = this.userId,
        title = this.title,
        feedURL = this.feedURL,
        siteURL = this.siteURL,
        iconURL = this.iconURL,
        errorCount = this.errorCount,
        errorMsg = this.errorMsg,
        folderId = this.folderId,
        hideGlobally = this.hideGlobally,
        onlyShowUnread = this.onlyShowUnread,
        orderx = this.order,
    )
}

fun FeedEntity.to(): Feed {
    return Feed.EMPTY
}