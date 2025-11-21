package cn.coolbet.orbit.model.domain

interface Meta {
    val siteURL: String
    val title: String
    val hideGlobally: Boolean
    val onlyShowUnread: Boolean
    val order: String
    val metaId: MetaId
    val url: String
}

data class MetaId(
    val type: String,
    val id: Long,
) {
    val isFeed: Boolean get() = type == "e"
    val isFolder: Boolean get() = type == "o"
    override fun toString(): String {
        return "$type$id"
    }
}