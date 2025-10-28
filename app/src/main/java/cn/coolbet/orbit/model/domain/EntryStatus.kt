package cn.coolbet.orbit.model.domain

enum class EntryStatus(val status: String) {
    UNREAD("unread"),
    READ("read");

    override fun toString(): String = status
}