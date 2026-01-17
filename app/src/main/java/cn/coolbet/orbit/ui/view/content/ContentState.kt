package cn.coolbet.orbit.ui.view.content

import androidx.compose.runtime.compositionLocalOf
import cn.coolbet.orbit.common.ConsumerUnit
import cn.coolbet.orbit.model.domain.Entry

data class ContentState(
    val entry: Entry = Entry.EMPTY,
    val readerModeOpened: Boolean = false,
    val index: Int = 0,
)

val LocalToggleReaderMode = compositionLocalOf<ConsumerUnit> {
    error("No function provided")
}
val LocalChangeStarred = compositionLocalOf<ConsumerUnit> {
    error("No function provided")
}
val LocalNextEntry = compositionLocalOf<() -> Entry?> {
    error("No function provided")
}