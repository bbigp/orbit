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
class AddFeedState {

    var text by mutableStateOf("")

//    var isAdding by mutableStateOf(false)
//    val canSubmit: Boolean
//        get() = feedUrl.isNotBlank() && !isAdding
    fun updateText(v: String) { text = v }
//    fun updateTitle(v: String) { title = v }
//    fun updateCategory(f: Folder) { category = f }

}
