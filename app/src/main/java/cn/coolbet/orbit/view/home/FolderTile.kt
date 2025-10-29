package cn.coolbet.orbit.view.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.view.CountBadge
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black50

@Composable
fun FolderTile(folder: Folder) {
    Column {
        FolderRow(folder)
        SpacerDivider(Modifier.padding(start = 80.dp, end = 16.dp))
        AnimatedVisibility(
            visible = folder.expanded,
            enter = expandVertically(
                animationSpec = tween(durationMillis = 200, easing = EaseInOut)
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = 200, easing = EaseInOut)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .height((52 * folder.feeds.size).dp))
            {
                folder.feeds.forEach { feed ->
                    FeedTile(feed, hasIndicator = false)
                }
            }

        }

    }
}

@Composable
fun FolderRow(folder: Folder) {
    val onToggleExpanded = LocalExpandFolder.current
    Row (
        modifier = Modifier.height(52.dp).fillMaxWidth()
            .padding(start = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Box(
            modifier = Modifier.height(32.dp)
                .width(32.dp)
                .clickable(
                    onClick = { onToggleExpanded(folder.id) },
                    role = Role.Button
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = if (folder.expanded) R.drawable.triangle_down else R.drawable.triangle_right),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(Black50)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.group),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Inside,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = folder.title,
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
fun PreviewFolderTile() {
    val feed = Feed(id = 1, title = "少数派 - sspai")
    val folder = Folder(id = 1, title = "土豆", feeds = listOf(feed, feed))
    Column {
        FolderTile(folder)
        FolderTile(folder.copy(expanded = true))
    }
}