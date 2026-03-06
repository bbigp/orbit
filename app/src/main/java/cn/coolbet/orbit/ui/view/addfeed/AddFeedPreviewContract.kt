package cn.coolbet.orbit.ui.view.addfeed

import cn.coolbet.orbit.model.domain.Feed


data class AddFeedPreviewState(
    val preview: AddFeedPreview,
) {
    val feed: Feed = Feed.EMPTY.copy(
        title = preview.title,
        feedURL = preview.url,
        iconURL = preview.iconUrl,
    )
}
