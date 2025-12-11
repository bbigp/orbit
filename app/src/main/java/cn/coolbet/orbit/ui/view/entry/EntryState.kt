package cn.coolbet.orbit.ui.view.entry

import androidx.compose.runtime.compositionLocalOf
import cn.coolbet.orbit.common.ConsumerUnit
import cn.coolbet.orbit.model.domain.Entry

data class EntryState(
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
}

val LocalChangeReaderView = compositionLocalOf<ConsumerUnit> {
    error("No function provided")
}
val LocalChangeStarred = compositionLocalOf<ConsumerUnit> {
    error("No function provided")
}
val LocalNextEntry = compositionLocalOf<ConsumerUnit> {
    error("No function provided")
}