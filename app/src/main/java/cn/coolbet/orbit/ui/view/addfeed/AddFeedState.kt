package cn.coolbet.orbit.ui.view.addfeed

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.coolbet.orbit.model.domain.Folder

interface AddFeedActions {
    fun addFeed()
    fun reset()
}

@Stable
class AddFeedState(
    private val initialCategory: Folder = Folder.EMPTY,
) {
    var feedUrl by mutableStateOf("")
    var title by mutableStateOf("")
    var category by mutableStateOf(initialCategory)
    var isAdding by mutableStateOf(false)

    val canSubmit: Boolean
        get() = feedUrl.isNotBlank() && !isAdding

    fun updateFeedUrl(v: String) { feedUrl = v }
    fun updateTitle(v: String) { title = v }
    fun updateCategory(f: Folder) { category = f }

    fun reset() {
        feedUrl = ""
        title = ""
        category = initialCategory
    }
}
