package cn.coolbet.orbit.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media(
    val id: Long,
    val userId: Long,
    val entryId: Long,
    val url: String = "",
    val mimeType: String = "",
    val size: Int = 0,
): Parcelable {
}