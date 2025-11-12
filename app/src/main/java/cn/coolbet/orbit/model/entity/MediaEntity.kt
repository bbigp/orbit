package cn.coolbet.orbit.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.coolbet.orbit.model.domain.Media

@Entity(tableName = "medias")
data class MediaEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "user_id") val userId: Long = 0,
    @ColumnInfo(name = "entry_id") val entryId: Long = 0,
    @ColumnInfo(name = "url", defaultValue = "") val url: String = "",
    @ColumnInfo(name = "mime_type", defaultValue = "") val mimeType: String = "",
    @ColumnInfo(name = "size", defaultValue = "0") val size: Int = 0,
)


fun MediaEntity.to(): Media {
    return Media(
        id = id, userId = userId, entryId = entryId, url = url, mimeType = mimeType,
        size = size,
    )
}