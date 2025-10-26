package cn.coolbet.orbit.module.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.module.CountBadge
import cn.coolbet.orbit.module.FeedIcon
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black25
import cn.coolbet.orbit.ui.theme.ElementSize


@Composable
fun FeedTile(feed: Feed, hasIndicator: Boolean = true) {
    Row (
        modifier = Modifier.height(52.dp).fillMaxWidth()
            .padding(start = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        if (hasIndicator) LeadingIndicator() else Spacer(modifier = Modifier.width(32.dp).height(32.dp))
        Spacer(modifier = Modifier.width(4.dp))
        FeedIcon(url = feed.iconURL, alt = feed.title, size = ElementSize.MEDIUM)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = feed.title,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTypography.R15,
        )
        Box(modifier = Modifier.width(12.dp))
        CountBadge()
    }
}

@Preview(showBackground = true)
@Composable
fun LeadingIndicator() {
    Box(
        modifier = Modifier.height(32.dp)
            .width(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.dot_s),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(Black25)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFeedTile(){
    Column {
        FeedTile(feed = Feed(title = "少数派",
            iconURL = "https://cdn-static.sspai.com/favicon/sspai.ico",
        ))
        FeedTile(feed = Feed(title = "少数派",
            iconURL = "https://cdn-static.sspai.com/favicon/sspai.ico",
        ), hasIndicator = false)
    }
}