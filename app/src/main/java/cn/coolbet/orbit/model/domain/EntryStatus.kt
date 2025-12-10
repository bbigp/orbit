package cn.coolbet.orbit.model.domain

import androidx.room.TypeConverter

enum class EntryStatus(val value: String) {
    UNREAD("unread"),
    READ("read"),
    REMOVED("removed");

    override fun toString(): String = value

    companion object {
        fun from(status: String): EntryStatus {
            return EntryStatus.entries.find { it.value == status } ?: UNREAD
        }
    }

    val isUnread: Boolean get() = this == UNREAD
    val isRead: Boolean get() = this == READ
    val isRemoved: Boolean get() = this == REMOVED
}

class EntryStatusConverter {
    // 🌟 将 Enum 转换为 String (存储到数据库)
    @TypeConverter
    fun fromStatus(status: EntryStatus): String {
        return status.value
    }

    // 🌟 将 String 转换为 Enum (从数据库读取)
    @TypeConverter
    fun toStatus(statusName: String): EntryStatus {
        return EntryStatus.from(statusName)
    }
}