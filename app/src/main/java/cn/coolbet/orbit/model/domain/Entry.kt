package cn.coolbet.orbit.model.domain

import android.os.Parcelable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import cn.coolbet.orbit.common.HTMLProcessingHelper
import cn.coolbet.orbit.common.hasHtmlTags
import cn.coolbet.orbit.common.splitHtml
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
    val readerPageState: ReaderPageState = ReaderPageState.Idle,

    val feed: Feed = Feed.EMPTY,
    val medias: List<Media> = emptyList(),
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

    val segments: List<AnnotatedString> get() {
        val rawText = listOf(readableContent, content, summary)
            .firstOrNull { it.isNotEmpty() } ?: ""

        val finalHtml = when {
            rawText.isEmpty() -> "<p></p>"
            rawText.hasHtmlTags() -> rawText
            else -> "<p>$rawText</p>"
        }
        return mutableListOf<AnnotatedString>().apply {
            if (title.isNotEmpty()) {
                add(buildAnnotatedString { append(title) })
            }
            addAll(finalHtml.splitHtml(240 - title.length))
        }
    }

    val showReadMore: Boolean get() = segments.sumOf { it.length } + title.length >= 240
}



/**
 * 通用的列表元素更新函数
 * @param id 目标元素的 ID
 * @param transform 定义如何将旧元素转换为新元素
 */
fun List<Entry>.update(id: Long, transform: (Entry) -> Entry): List<Entry> {
    val index = this.indexOfFirst { it.id == id }
    if (index == -1) return this

    return this.toMutableList().apply {
        this[index] = transform(this[index])
    }
}