package cn.coolbet.orbit.ui.view.feed

import cn.coolbet.orbit.R

enum class EditFeedBackAction {
    POP_SCREEN,
    HIDE_SHEET,
}

data class EditFeedSheetConfig(
    val dragMode: EditFeedDragMode = EditFeedDragMode.STATIC,
    // 是否允许“展开/收起”这个能力
    val expandable: Boolean = false,
    // 如果允许展开，初始是展开还是收起
    val expandableInitialExpanded: Boolean = true,
    val bottomButtonsLayout: EditFeedBottomButtonsLayout = EditFeedBottomButtonsLayout.VERTICAL_TWO,
    val topBarBackIconId: Int = R.drawable.arrow_left,
    val backAction: EditFeedBackAction = EditFeedBackAction.POP_SCREEN,
)

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
