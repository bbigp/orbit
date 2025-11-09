package cn.coolbet.orbit.model.domain

enum class EntryStatus(val valaue: String) {
    UNREAD("unread"),
    READ("read"),
    REMOVED("removed");

    override fun toString(): String = valaue

    companion object {
        fun from(status: String): EntryStatus {
            return EntryStatus.entries.find { it.valaue == status } ?: UNREAD
        }
    }
}