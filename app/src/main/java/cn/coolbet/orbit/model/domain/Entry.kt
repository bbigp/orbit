package cn.coolbet.orbit.model.domain

import android.os.Parcelable
import androidx.compose.ui.text.AnnotatedString
import cn.coolbet.orbit.common.HTMLProcessingHelper
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Entry(
    val id: Long,
    val userId: Long,
    val feedId: Long = 0,
    val status: EntryStatus = EntryStatus.UNREAD,
    val hash: String = "",
    val title: String = "",
    val url: String = "",
    val publishedAt: Long = 0,
    val content: String = "",
    val author: String = "",
    val starred: Boolean = false,
    val readingTime: Int = 0,
    val createdAt: Long = 0,
    val changedAt: Long = 0,
    val tags: List<String> = emptyList(),

    val summary: String = "",
    val readableContent: String = "",
    val leadImageURL: String = "",

    val feed: Feed = Feed.EMPTY,
    val medias: List<Media> = emptyList(),
    @IgnoredOnParcel val segments: List<AnnotatedString> = mutableListOf()
): Parcelable {
    companion object {
        val EMPTY = Entry(id = 0, userId = 0)
    }

    val isEmpty: Boolean get() = id == 0L
    val isUnread: Boolean get() = status == EntryStatus.UNREAD
    val pic: String get() {
        if (leadImageURL.isNotEmpty()) return leadImageURL
//        medias
        return ""
    }
    val description: String get() {
        return summary
        //content remove html tag
    }
    val insertHeroImage: Boolean get() {
        return HTMLProcessingHelper.process(readableContent).insertHeroImage
    }
}


fun List<Entry>.replace(newItem: Entry): List<Entry> {
    val index = this.indexOfFirst { it.id == newItem.id }

    if (index == -1) {
        return this
    }
    return this.toMutableList().apply {
        this[index] = newItem
    }
    // 注意：toList() 隐式地在返回时创建了不可变列表
}