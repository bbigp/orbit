package cn.coolbet.orbit.ui.view.entries

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.showTime
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.view.FeedIcon
import cn.coolbet.orbit.ui.view.FeedIconDefaults
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent

@Composable
fun EntryTile(entry: Entry) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        EntryTileTopRow(entry)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    entry.title,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    style = AppTypography.M15,
                )
                if (entry.summary.isNotEmpty()) {
                    Text(
                        entry.summary,
                        maxLines = 2, overflow = TextOverflow.Ellipsis,
                        style = AppTypography.R13B50,
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }
            }
            if (entry.pic.isNotEmpty()) {
                SubcomposeAsyncImage(
                    model = entry.pic,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 0.5.dp,
                            color = Black08,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    loading = {
                        Box(modifier = Modifier.fillMaxSize().pulsatingShimmer(true))
                    },
                    error = {
                        Image(
                            painter = painterResource(R.drawable.no_media),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    },
                    success = {
                        SubcomposeAsyncImageContent()
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * 带有基础脉冲效果的 Shimmer 骨架屏 Modifier。
 * 当 isLoading 为 true 时，应用一个不断循环的灰色背景透明度变化动画。
 * 这是一个 Modifier 扩展函数，用于实现自定义视觉效果。
 */
fun Modifier.pulsatingShimmer(isLoading: Boolean): Modifier = composed {
    if (!isLoading) return@composed this

    // 创建一个无限循环的动画
    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val alpha by transition.animateFloat(
        initialValue = 0.4f, // 初始透明度
        targetValue = 0.7f,  // 目标透明度
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing), // 800ms 匀速动画
            repeatMode = RepeatMode.Reverse // 来回重复
        ), label = "ShimmerAlpha"
    )

    // 使用带有脉冲透明度的浅灰色作为背景
    val shimmerColor = Color.LightGray.copy(alpha = alpha)

    this.then(
        Modifier.background(shimmerColor)
    )
}

@Composable
fun EntryTileTopRow(entry: Entry){
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(16.dp))
        FeedIcon(entry.feed.iconURL, entry.feed.title, size = FeedIconDefaults.SMALL)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            entry.feed.title,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = AppTypography.M13,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            entry.publishedAt.showTime(),
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = AppTypography.M13B25,
            modifier = Modifier.wrapContentWidth()
        )
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEntryTile() {
    val feed = Feed(title = "少数派 - sspai", id = 1)
    val entry = Entry.EMPTY.copy(feed = feed, publishedAt = System.currentTimeMillis(),
        title = "心率监测、降噪隔音、空间音频，AirPods Pro 3 从日常到运动全能体验报告",
        leadImageURL = "https://image.zhangxinxu.com/image/blog/202507/2025-7-15_145617.png",
        summary = "少数派的近期动态少数派11月主题征稿进行中：平台独占KillerApp、聊聊卫星通讯。投稿有奖励GAMEBABYforiPhone17系列现已上市。进一步了解《蓝皮书》系列新版上架，一起探索全新iOS ..."
    )
    Column {
        EntryTileTopRow(entry)
        SpacerDivider()
        EntryTile(entry.copy(summary = "", leadImageURL = ""))
        SpacerDivider()
        EntryTile(entry.copy(leadImageURL = ""))
        SpacerDivider()
        EntryTile(entry.copy(summary = ""))
        SpacerDivider()
        EntryTile(entry)
    }
}