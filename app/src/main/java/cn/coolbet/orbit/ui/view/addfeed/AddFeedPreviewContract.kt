package cn.coolbet.orbit.ui.view.addfeed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.coolbet.orbit.model.domain.Feed

class AddFeedPreviewArgs

sealed class AddFeedPreviewAction {
    object Subscribe : AddFeedPreviewAction()
}

data class AddFeedPreviewUnit(
    val none: Unit = Unit,
)

data class AddFeedPreviewState(
    val preview: AddFeedPreview,
) {
    var feedId by mutableLongStateOf(preview.feedId)
    var isSubmitting by mutableStateOf(false)

    val feed: Feed
        get() = Feed.EMPTY.copy(
            id = feedId,
            title = preview.title,
            feedURL = preview.url,
            iconURL = preview.iconUrl,
        )
}

sealed class AddFeedPreviewEffect {
    data class Subscribed(val feedId: Long) : AddFeedPreviewEffect()
    data class Error(val message: String) : AddFeedPreviewEffect()
}
