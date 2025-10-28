package cn.coolbet.orbit.model.domain

interface Meta {
    val siteURL: String
    val title: String
    val hideGlobally: Boolean
    val onlyShowUnread: Boolean
    val order: String
    val metaId: String
    val url: String
}