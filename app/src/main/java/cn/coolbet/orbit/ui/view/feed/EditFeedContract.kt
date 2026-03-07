package cn.coolbet.orbit.ui.view.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder

enum class EditFeedDragMode {
    NONE,
    STATIC,
}

enum class EditFeedCloseMode {
    POP,
    HIDE_SHEET,
}

data class EditFeedArgs(
    val dragMode: EditFeedDragMode = EditFeedDragMode.STATIC,
    val topBarBackIconId: Int = R.drawable.arrow_left,
    val closeMode: EditFeedCloseMode = EditFeedCloseMode.POP,
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

    val isModified: Boolean
        get() = title != feed.title || category.id != feed.folder.id

    fun updateTitle(v: String) { title = v }
    fun updateCategory(f: Folder) { category = f }

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
