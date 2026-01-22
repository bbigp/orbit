package cn.coolbet.orbit.ui.view.listdetail.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.common.copyText
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.ui.kit.ObToastManager
import cn.coolbet.orbit.ui.kit.ToastType
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black04
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.Black25
import cn.coolbet.orbit.ui.view.FeedIcon
import cn.coolbet.orbit.ui.view.FeedIconDefaults

@Preview(showBackground = true)
@Composable
fun PreviewEditFeedView() {
    val meta = Feed.EMPTY.copy(title = "少数派 - sspai", feedURL = "htts://sspai.com/feed")
    Column {
        EditFeedCardView(meta)
    }
}


@Composable
fun EditFeedCardView(
    meta: Meta,
    onNavigateToEditFeed: () -> Unit = {}
) {
    val context = LocalContext.current
    Column (
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = Black08, shape = RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            FeedIcon(url = meta.iconURL, alt = meta.title, size = FeedIconDefaults.LARGE)

            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(meta.title, style = AppTypography.M15, maxLines = 1)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(meta.url, style = AppTypography.R13B50, maxLines = 1)
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        modifier = Modifier.size(16.dp)
                            .click {
                                copyText(context, meta.url)
                                ObToastManager.show("Link copied", type = ToastType.Success)
                            },
                        painter = painterResource(id = R.drawable.copy),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Black25)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Box(
                modifier = Modifier.height(48.dp).width(60.dp)
                    .background(Black04, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .click(onClick = onNavigateToEditFeed),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                )
            }


            Spacer(modifier = Modifier.width(6.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}