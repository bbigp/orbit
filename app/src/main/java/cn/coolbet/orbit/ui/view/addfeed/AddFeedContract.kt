package cn.coolbet.orbit.ui.view.addfeed

import cn.coolbet.orbit.model.domain.Entry

class AddFeedArgs

sealed class AddFeedAction {
    object FetchPreview : AddFeedAction()
    data class Subscribe(val preview: AddFeedPreview) : AddFeedAction()
    data class Unsubscribe(val preview: AddFeedPreview) : AddFeedAction()
}

data class AddFeedPreview(
    val title: String,
    val feedId: Long = 0,
    val url: String,
    val iconUrl: String,
    val subscribeState: AddFeedSubscribeState = AddFeedSubscribeState.NOT_SUBSCRIBED,
    val entries: List<Entry> = emptyList(),
)

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
