package cn.coolbet.orbit.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "search_records")
data class SearchRecord(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo("user_id") val userId: Long = 0,
    @ColumnInfo("meta_id") val metaId: String = "",
    @ColumnInfo("word") val word: String = "",
    @ColumnInfo(name = "created_at", defaultValue = "0") val createdAt: Long = Date().time,
    @ColumnInfo(name = "changed_at", defaultValue = "0") val changedAt: Long = Date().time
)