package cn.coolbet.orbit.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
import cn.coolbet.orbit.model.domain.ReaderPageState
import java.util.Date

@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "user_id") val userId: Long = 0,
    @ColumnInfo(name = "hash") val hash: String = "",
    @ColumnInfo(name = "feed_id", defaultValue = "0") val feedId: Long = 0,
    @ColumnInfo(name = "status", defaultValue = "unread") val status: String = "unread",
    @ColumnInfo(name = "title", defaultValue = "") val title: String = "",
    @ColumnInfo(name = "url", defaultValue = "") val url: String = "",
    @ColumnInfo(name = "published_at", defaultValue = "0") val publishedAt: Long = Date().time,
    @ColumnInfo(name = "content", defaultValue = "") val content: String = "",
    @ColumnInfo(name = "author", defaultValue = "") val author: String = "",
    @ColumnInfo(name = "starred", defaultValue = "false") val starred: Boolean = false,
    @ColumnInfo(name = "reading_time", defaultValue = "0") val readingTime: Int = 0,
    @ColumnInfo(name = "tags", defaultValue = "") val tags: String = "",
    @ColumnInfo(name = "created_at", defaultValue = "0") val createdAt: Long = Date().time,
    @ColumnInfo(name = "changed_at", defaultValue = "0") val changedAt: Long = Date().time,

    @ColumnInfo(name = "summary", defaultValue = "") val summary: String = "",
    @ColumnInfo(name = "readable_content", defaultValue = "") val readableContent: String = "",
    @ColumnInfo(name = "lead_image_url", defaultValue = "") val leadImageURL: String = "",
    @ColumnInfo(name = "reader_page_state", defaultValue = "Idle") val readerPageState: ReaderPageState = ReaderPageState.Idle,
)


fun EntryEntity.to(): Entry {
    return Entry(
        id = id, userId = userId, hash = hash, feedId = feedId, status = EntryStatus.from(status),
        title = title, url = url, publishedAt = publishedAt, content = content, author = author,
        starred = starred, readingTime = readingTime, tags = tags.split(","), createdAt = createdAt, changedAt = createdAt,
        summary = summary, readableContent = readableContent, leadImageURL = leadImageURL,
        readerPageState = readerPageState
    )
}
