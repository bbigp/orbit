package cn.coolbet.orbit.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sync_task_records")
data class SyncTaskRecord(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo("user_id") val userId: Long = 0,
    @ColumnInfo("status", defaultValue = "running") val status: String = "running",
    @ColumnInfo(name = "execute_time", defaultValue = "0") val executeTime: Long = Date().time,

    @ColumnInfo(name = "from_time") val fromTime: Long = 0,
    @ColumnInfo(name = "to_time") val toTime: Long = 0,

    @ColumnInfo(name = "error_msg", defaultValue = "") val errorMsg: String = "",
    @ColumnInfo(name = "feed", defaultValue = "0") val feed: Int = 0,
    @ColumnInfo(name = "folder", defaultValue = "0") val folder: Int = 0,
    @ColumnInfo(name = "media", defaultValue = "0") val media: Int = 0,
    @ColumnInfo(name = "entry", defaultValue = "0") val entry: Int = 0,

    @ColumnInfo(name = "created_at", defaultValue = "0") val createdAt: Long = Date().time,
    @ColumnInfo(name = "changed_at", defaultValue = "0") val changedAt: Long = Date().time,
) {
    companion object {
        const val RUNNING = "running"
        const val OK = "ok"
        const val FAIL = "fail"
    }
}
