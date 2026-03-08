package cn.coolbet.orbit.ui.view.addfeed

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.parcelize.Parcelize

@Parcelize
class AddFeedPreviewArgs : Parcelable

sealed class AddFeedPreviewAction {
    object Subscribe : AddFeedPreviewAction()
}

data class AddFeedPreviewUnit(
    val none: Unit = Unit,
)

data class AddFeedPreviewState(
    val preview: AddFeedPreview,
) {
    var isSubmitting by mutableStateOf(false)
    var feed by mutableStateOf(preview.feed)
}

sealed class AddFeedPreviewEffect {
    data class Subscribed(val feedId: Long) : AddFeedPreviewEffect()
    data class Error(val message: String) : AddFeedPreviewEffect()
}
