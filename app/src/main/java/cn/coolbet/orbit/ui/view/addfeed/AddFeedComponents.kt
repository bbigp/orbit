package cn.coolbet.orbit.ui.view.addfeed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black16
import cn.coolbet.orbit.ui.theme.ObTheme
import cn.coolbet.orbit.ui.view.FeedIcon
import cn.coolbet.orbit.ui.view.FeedIconDefaults

@Composable
internal fun AddFeedPreviewList(
    items: List<AddFeedPreview>,
    onItemClick: (AddFeedPreview) -> Unit,
    onSubscribeClick: (AddFeedPreview, AddFeedSubscribeState) -> Unit,
) {
    Column {
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .click { onItemClick(item) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FeedIcon(
                    url = item.feed.iconURL,
                    alt = item.feed.title,
                    size = FeedIconDefaults.LARGE,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.feed.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = AppTypography.M15,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        item.feed.feedURL,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = AppTypography.R13B50,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                AddSubscribeIcon(
                    state = item.subscribeState,
                    onClick = { onSubscribeClick(item, item.subscribeState) }
                )
            }
        }
    }
}

@Composable
internal fun AddSubscribeIcon(
    state: AddFeedSubscribeState,
    size: Dp = 28.dp,
    onClick: () -> Unit = {},
) {
    when (state) {
        AddFeedSubscribeState.NOT_SUBSCRIBED -> {
            ObIcon(
                id = R.drawable.plus_fill,
                modifier = Modifier.size(size).click(onClick = onClick),
            )
        }
        AddFeedSubscribeState.SUBSCRIBED -> {
            ObIcon(
                id = R.drawable.check_fill,
                modifier = Modifier.size(size).click(onClick = onClick),
                color = Black16
            )
        }
        AddFeedSubscribeState.SUBSCRIBING -> {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(19.dp),
                    strokeWidth = 2.dp,
                    color = ObTheme.colors.primary,
                )
            }
        }
    }
}
