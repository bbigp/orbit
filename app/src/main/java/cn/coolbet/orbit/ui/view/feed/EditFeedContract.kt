package cn.coolbet.orbit.ui.view.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.R

enum class EditFeedBackAction {
    POP_SCREEN,
    HIDE_SHEET,
}

enum class EditFeedDragMode {
    NONE,
    STATIC,
    TOGGLE,
}

enum class EditFeedBottomButtonsLayout {
    NONE,
    HORIZONTAL_TWO,
    VERTICAL_TWO,
}

data class EditFeedArgs(
    val dragMode: EditFeedDragMode = EditFeedDragMode.STATIC,
    // 是否允许用户折叠/展开内容区域。
    val collapsible: Boolean = false,
    // 内容区域初始是否展开，仅在 `collapsible = true` 时生效。
    val initiallyExpanded: Boolean = true,
    val bottomButtonsLayout: EditFeedBottomButtonsLayout = EditFeedBottomButtonsLayout.VERTICAL_TWO,
    val topBarBackIconId: Int = R.drawable.arrow_left,
    val backAction: EditFeedBackAction = EditFeedBackAction.POP_SCREEN,
)

sealed class EditFeedAction {
    object ApplyChanges : EditFeedAction()
    object Unsubscribe : EditFeedAction()
}

data class EditFeedState(
    val feed: Feed,
) {
    var title by mutableStateOf(feed.title)
    var category by mutableStateOf(feed.folder)
    var expanded by mutableStateOf(true)

    val isModified: Boolean
        get() = title != feed.title || category.id != feed.folder.id

    fun updateTitle(v: String) { title = v }
    fun updateCategory(f: Folder) { category = f }
    fun updateExpanded(v: Boolean) { expanded = v }
    fun toggleExpanded() { expanded = !expanded }

    var isApplying by mutableStateOf(false)
    var isUnsubscribing by mutableStateOf(false)
}

sealed class EditFeedEffect {
    object Unsubscribed : EditFeedEffect()
    data class Error(val message: String) : EditFeedEffect()
}

data class EditFeedUnit(
    val folders: List<Folder> = emptyList(),
)
