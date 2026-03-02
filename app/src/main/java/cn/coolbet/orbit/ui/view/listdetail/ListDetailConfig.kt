package cn.coolbet.orbit.ui.view.listdetail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListDetailConfig(
    val showSearch: Boolean = true,
    val enableSwipe: Boolean = true,
    val moreAction: ListDetailMoreAction = ListDetailMoreAction.OPEN_SETTINGS,
) : Parcelable

enum class ListDetailMoreAction {
    OPEN_SETTINGS,
    OPEN_EDIT_FEED,
}

