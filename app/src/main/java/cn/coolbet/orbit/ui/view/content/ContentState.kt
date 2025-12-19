package cn.coolbet.orbit.ui.view.content

import android.os.Parcelable
import androidx.compose.runtime.compositionLocalOf
import cn.coolbet.orbit.common.ConsumerUnit
import cn.coolbet.orbit.model.domain.Entry
import kotlinx.parcelize.Parcelize

data class ContentState(
    val entry: Entry = Entry.EMPTY,
    val readingModeEnabled: Boolean = false,
    val readerView: Boolean = false,
    val isLoadingReadableContent: Boolean = false,
    val index: Int = 0,
)

interface PagingStateAccessor {
    val items: List<Entry>
    val total: Int
    val hasMore: Boolean
    suspend fun nextPage()
    fun indexOfFirst(item: Entry): Int
    fun entry(id: Long): Entry?
}

val LocalChangeReaderView = compositionLocalOf<ConsumerUnit> {
    error("No function provided")
}
val LocalChangeStarred = compositionLocalOf<ConsumerUnit> {
    error("No function provided")
}
val LocalNextEntry = compositionLocalOf<() -> Entry?> {
    error("No function provided")
}

@Parcelize
data class QueryContext(
    val page: String
): Parcelable {
    companion object {
        val normal = QueryContext(page = "normal")
        val search = QueryContext(page = "search")
    }

}