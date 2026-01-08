package cn.coolbet.orbit.model.domain

import android.os.Parcelable
import cn.coolbet.orbit.model.OrderPublishedAt
import cn.coolbet.orbit.model.entity.LDSettings
import kotlinx.parcelize.Parcelize
import okhttp3.internal.http2.Settings

@Parcelize
data class Folder (
    override val id: Long,
    val userId: Long = 0,
    override val title: String = "",

    val expanded: Boolean = false,
    val feeds: List<Feed> = emptyList(),
    override val settings: LDSettings = LDSettings.defaultSettings
): Meta {

    companion object {
        val EMPTY = Folder(id = 0, title = "None")
    }

    override val metaId: MetaId get() = MetaId("o", id)
    override val feedIds: List<Long> get() = feeds.map { it.id }.toList()
}


