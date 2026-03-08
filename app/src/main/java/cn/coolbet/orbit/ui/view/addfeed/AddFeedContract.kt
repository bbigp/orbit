package cn.coolbet.orbit.ui.view.addfeed

import android.os.Parcelable
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import kotlinx.parcelize.Parcelize

@Parcelize
class AddFeedArgs : Parcelable

sealed class AddFeedAction {
    object FetchPreview : AddFeedAction()
    data class Subscribe(val preview: AddFeedPreview) : AddFeedAction()
    data class Unsubscribe(val preview: AddFeedPreview) : AddFeedAction()
}

@Parcelize
data class AddFeedPreview(
    val feed: Feed,
    val subscribeState: AddFeedSubscribeState = AddFeedSubscribeState.NOT_SUBSCRIBED,
    val entries: List<Entry> = emptyList(),
) : Parcelable

enum class AddFeedSubscribeState {
    NOT_SUBSCRIBED,
    SUBSCRIBED,
    SUBSCRIBING,
}

data class AddFeedUnit(
    val previews: List<AddFeedPreview> = emptyList(),
    val error: String? = null,
)

sealed class AddFeedEffect {
    data class Success(val message: String) : AddFeedEffect()
    data class Error(val message: String) : AddFeedEffect()
}
