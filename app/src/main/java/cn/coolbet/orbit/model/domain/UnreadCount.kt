package cn.coolbet.orbit.model.domain

import androidx.room.ColumnInfo

data class UnreadCount(
    @ColumnInfo(name = "feed_id") val feedId: Long = 0,
    val count: Int = 0,
)