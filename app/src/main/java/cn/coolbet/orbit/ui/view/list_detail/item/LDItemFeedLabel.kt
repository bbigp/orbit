package cn.coolbet.orbit.ui.view.list_detail.item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.common.showTime
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.FeedIcon
import cn.coolbet.orbit.ui.view.FeedIconDefaults

@Composable
fun LDItemFeedLabel(
    feed: Feed,
    time: Long,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        FeedIcon(url = feed.iconURL, alt = feed.title, size = FeedIconDefaults.SMALL)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            feed.title,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = AppTypography.M13,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            time.showTime(),
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = AppTypography.M13B25,
            modifier = Modifier.wrapContentWidth()
        )
        Spacer(modifier = Modifier.width(16.dp))
    }
}