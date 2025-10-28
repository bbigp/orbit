package cn.coolbet.orbit.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.coolbet.orbit.model.domain.Folder

@Entity(tableName = "folders")
data class FolderEntity (
    @PrimaryKey val id: Long,
    @ColumnInfo("user_id") val userId: Long = 0,
    @ColumnInfo("title") val title: String = "",
    @ColumnInfo("hide_globally", defaultValue = "false") val hideGlobally: Boolean = false,
) {

    companion object {
        fun fromResponse(): FolderEntity {
            return FolderEntity(id = 0)
        }
    }

}

fun Folder.toEntity(): FolderEntity {
    return FolderEntity(
        id = this.id, userId = this.userId, title = this.title,
        hideGlobally = this.hideGlobally,
    )
}

fun FolderEntity.to(): Folder {
    return Folder(
        id = this.id, userId = this.userId, title = this.title,
        hideGlobally = this.hideGlobally,
    )
}