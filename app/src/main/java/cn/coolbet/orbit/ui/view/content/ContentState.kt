package cn.coolbet.orbit.ui.view.content

import androidx.compose.runtime.compositionLocalOf
import cn.coolbet.orbit.common.ConsumerUnit
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.entity.LDSettings

sealed class ContentAction {
    object ToggleReaderMode : ContentAction()
    object ChangeStarred : ContentAction()
    object OpenPreviousEntry : ContentAction()
    object OpenNextEntry : ContentAction()
}

sealed class ContentEffect {
    data class NavigateToEntry(val entry: Entry) : ContentEffect()
}

data class ContentState(
    val entry: Entry = Entry.EMPTY,
    val isReaderModeEnabled: Boolean = false,
    val index: Int = 0,
    val settings: LDSettings = LDSettings.defaultSettings,
    val entryTransitionDirection: EntryTransitionDirection = EntryTransitionDirection.None,
)

enum class EntryTransitionDirection {
    None,
    Previous,
    Next,
}

val LocalToggleReaderMode = compositionLocalOf<ConsumerUnit> {
    error("No function provided")
}
val LocalChangeStarred = compositionLocalOf<ConsumerUnit> {
    error("No function provided")
}
val LocalOpenNextEntry = compositionLocalOf<ConsumerUnit> {
    error("No function provided")
}
