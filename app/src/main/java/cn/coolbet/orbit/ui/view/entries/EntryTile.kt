package cn.coolbet.orbit.ui.view.entries

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.common.showTime
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black04
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.Black50
import cn.coolbet.orbit.ui.view.FeedIcon
import cn.coolbet.orbit.ui.view.FeedIconDefaults
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


// 定义操作阈值 (DP)
val SwipeActionThresholdDp = 38.dp // 短滑阈值
val ActionTriggerMaxDp = 70.dp    // 短滑最大触发范围


@Composable
fun SwipeWrapper(
    content: @Composable () -> Unit
){
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // 转换为像素 (PX)
    val shortPx = with(density) { SwipeActionThresholdDp.toPx() }     // 40dp
    val maxActionPx = with(density) { ActionTriggerMaxDp.toPx() }     // 120dp

    // 🌟 新状态：记录操作是否已执行（用于控制图标的即时消失/回弹）
//    var isActionExecuted by remember { mutableStateOf(false) }

    // 2. 存储当前偏移量，使用 Animatable 允许动画回弹
    val offsetX = remember { Animatable(0f) }

    // 3. 定义拖动状态 (onDelta 负责实时更新位置)
    val draggableState = rememberDraggableState(onDelta = { delta ->
        coroutineScope.launch {
            // 确保在拖动时重置操作状态
//            if (isActionExecuted) isActionExecuted = false
            // 限制向左滑动，并限制最大滑动距离（防止视图无限滑出）
            val newOffset = (offsetX.value + delta).coerceIn(0f, maxActionPx)
            offsetX.snapTo(newOffset)
        }
    })

    // 4. 定义回弹函数 (使用协程动画)
    fun animateBack() {
        coroutineScope.launch {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300) // 300ms 回弹
            ).apply {
                // 🌟 回弹完成后，重置状态
//                isActionExecuted = false
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        // 🌟 视觉反馈层：传入所有阈值
        SwipeActionsOverlay(
            currentOffset = offsetX.value,
//            isActionExecuted = isActionExecuted, // 🌟 传递新状态
            shortPx = shortPx,
//            maxActionPx = maxActionPx,
            startIcon = R.drawable.check_o,
            endIcon = R.drawable.unread
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                // 🌟 应用 offset 使内容跟随手指滑动
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        val finalOffset = offsetX.value

                        // --- 左滑操作 (offsetX < 0) ---
                        if (finalOffset in shortPx..maxActionPx) {
//                            isActionExecuted = true
                            // 左短滑：已读
                            Log.d("Swipe", "左短滑 for item")
                        }

                        // 无论是否触发操作，松手后都需要回弹
                        animateBack()
                    }
                )
        ) {
            content()
        }
    }

}


@Composable
fun SwipeActionsOverlay(
    currentOffset: Float,
//    isActionExecuted: Boolean,
    shortPx: Float,
//    maxActionPx: Float,
    startIcon: Int,
    endIcon: Int
) {
    // 2. 初始化 Icon 属性
    var icon: Int
    var iconColor: Color
    var containerColor: Color

    // 3. 判断当前处于哪个操作阶段
//    if (isActionExecuted) {
//        icon = endIcon
//        containerColor = Color(0xFF28CD41)
//        iconColor = Color.White
//        val backgroundAlpha = (currentOffset / maxActionPx).coerceIn(0f, 1f)
//        return Box(
//            modifier = Modifier.padding(start = 20.dp)
//                .size(32.dp)
//                .clip(CircleShape)
//                .background(containerColor.copy(backgroundAlpha)),
//            contentAlignment = Alignment.Center
//        ) {
//            Image(
//                modifier = Modifier.size(20.dp).graphicsLayer(alpha = backgroundAlpha),
//                painter = painterResource(id = icon),
//                contentDescription = "",
//                contentScale = ContentScale.Fit,
//                colorFilter = ColorFilter.tint(iconColor),
//            )
//        }
//    } else {
        when {
            // 阶段 B: 右短滑颜色渐变 (40dp - 120dp)
            currentOffset > shortPx -> {
                icon = endIcon
                containerColor = Color(0xFF28CD41)
                iconColor = Color.White
            }
            // 阶段 A: 右短滑 Icon 渐显 (0dp - 40dp)
            currentOffset > 0f -> {
                icon = startIcon
                containerColor = Black08
                iconColor = Black50
            }
            else -> return // 不滑动，不渲染
        }
//    }

    // 5. 渲染操作区域
    Column {
        Text("$currentOffset")
        Box(
            modifier = Modifier.padding(start = 20.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = icon),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(iconColor),
            )
        }
    }

}

@Composable
fun EntryTile(entry: Entry) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.click(
            {
                NavigatorBus.push(Route.Entry(entry))
            }
        )
    ) {
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
                    model = ImageRequest.Builder(context)
                        .data(entry.pic)
                        .httpHeaders(NetworkHeaders.Builder().add("Referer", entry.url).build())
                        .build(),
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

        Box(
            modifier = Modifier.padding(start = 20.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Black08),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.check_o),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Black50),
            )
        }

        Box(
            modifier = Modifier.padding(start = 20.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF28CD41)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.unread),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White),
            )
        }
    }
}