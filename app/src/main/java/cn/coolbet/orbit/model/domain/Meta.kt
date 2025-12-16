package cn.coolbet.orbit.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Meta: Parcelable {
    val id: Long
    val siteURL: String get() = ""
    val title: String
    val hideGlobally: Boolean
    val onlyShowUnread: Boolean
    val order: String
    val metaId: MetaId
    val url: String get() = ""
    val feedIds: List<Long>
    val statuses: List<EntryStatus> get() {
        return if (onlyShowUnread) {
            listOf(EntryStatus.UNREAD)
        } else {
            listOf(EntryStatus.UNREAD, EntryStatus.READ)
        }
    }
    val iconURL: String get() = ""
    val recentPubTime: Int get() = 0
    val recentAddTime: Int get() = 0

    val isNotEmpty: Boolean get() = id != 0L
}

@Parcelize
data class MetaId(
    val type: String,
    val id: Long,
) : Parcelable {
    val isFeed: Boolean get() = type == "e"
    val isFolder: Boolean get() = type == "o"
    override fun toString(): String {
        return "$type$id"
    }
}