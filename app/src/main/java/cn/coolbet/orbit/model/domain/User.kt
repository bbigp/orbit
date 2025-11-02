package cn.coolbet.orbit.model.domain

import android.R.bool


data class User(
    val id: Long,
    val username: String = "",
    val token: String,
    val baseURL: String,
    val autoRead: Boolean = false,
    val unreadMark: UnreadMark = UnreadMark.NUMBER,
    val openContent: String = "",
    val rootFolder: Long = 0,
) {


}


enum class UnreadMark(val value: String) {
    NUMBER("Number"),
    DOT("Dot"),
    NONE("None");
    companion object {
        fun fromValue(value: String?): UnreadMark =
            UnreadMark.entries.find { it.value == value } ?: NUMBER
    }
}