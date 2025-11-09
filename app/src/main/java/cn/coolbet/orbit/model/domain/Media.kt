package cn.coolbet.orbit.model.domain

data class Media(
    val id: Long,
    val userId: Long,
    val entryId: Long,
    val url: String = "",
    val mimeType: String = "",
    val size: Int = 0,
) {
}