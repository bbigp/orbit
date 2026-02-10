package cn.coolbet.orbit.ui.view.feed

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder

interface EditFeedActions {
    fun applyChanges()
    fun unsubscribe()
}


@Stable
class EditFeedState(
    val feed: Feed,
    private val initialCategory: Folder
) {
    var title by mutableStateOf(feed.title)
    var category by mutableStateOf(initialCategory)
    // loading flags for async actions (managed by ScreenModel)
    var isApplying by mutableStateOf(false)
    var isUnsubscribing by mutableStateOf(false)

    private val originalFolderId: Long = feed.folderId
    private val originalTitle: String = feed.title

    val isModified: Boolean
        get() = originalFolderId != category.id || originalTitle != title

    fun reset() {
        title = originalTitle
        category = initialCategory
    }

    fun updateTitle(v: String) { title = v }
    fun updateCategory(f: Folder) { category = f }

}
