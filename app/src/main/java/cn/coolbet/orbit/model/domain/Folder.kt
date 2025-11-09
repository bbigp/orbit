package cn.coolbet.orbit.model.domain

import cn.coolbet.orbit.model.OrderPublishedAt

data class Folder (
    val id: Long,
    val userId: Long = 0,
    override val title: String = "",
    override val hideGlobally: Boolean = false,
    override val order: String = OrderPublishedAt,
    override val onlyShowUnread: Boolean = false,

    val expanded: Boolean = false,
    val feeds: List<Feed> = emptyList(),
): Meta {

    companion object {
        val EMPTY = Folder(id = 0, title = "None")
    }

    override val metaId: String get() = "o$id"
    override val siteURL: String get() = ""
    override val url: String = ""
    val statuses: List<String> = if (onlyShowUnread) listOf(EntryStatus.UNREAD.valaue) else listOf(
        EntryStatus.UNREAD.valaue,
        EntryStatus.READ.valaue
    )
}


