package cn.coolbet.orbit.ui.view.addfeed

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue

@Stable
class AddFeedState {
    var inputUrl by mutableStateOf(TextFieldValue("https://www.gcores.com/rss"))
    var isFetchingPreview by mutableStateOf(false)

    fun updateInputUrl(v: TextFieldValue) { inputUrl = v }
    fun setLoading(v: Boolean) { isFetchingPreview = v }
}
