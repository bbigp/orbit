package cn.coolbet.orbit.model.domain

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
}