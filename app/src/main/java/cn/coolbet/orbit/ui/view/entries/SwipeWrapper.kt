package cn.coolbet.orbit.ui.view.entries

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.Black50
import cn.coolbet.orbit.ui.theme.Blue
import cn.coolbet.orbit.ui.theme.Green
import cn.coolbet.orbit.ui.theme.Purple
import cn.coolbet.orbit.ui.theme.Yellow
import kotlinx.coroutines.launch
import java.time.Year
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


// 定义操作阈值 (DP)
val SwipeActionThresholdDp = 38.dp // 短滑阈值
val ActionTriggerMaxDp = 100.dp    // 短滑最大触发范围

val UnreadStateDefinition = SwipeStateDefinition(
    icon = R.drawable.unread,
    containerColor = Green,
)
val ReadStateDefinition = SwipeStateDefinition(
    icon = R.drawable.check_o,
    containerColor = Green,
)

val NoneStateDefinition = SwipeStateDefinition(
    icon = R.drawable.ban,
    containerColor = Blue,
)

val OpenBrowserStateDefinition = SwipeStateDefinition(
    icon = R.drawable.out_o,
    containerColor = Purple,
)

val AddCollectionStateDefinition = SwipeStateDefinition(
    icon = R.drawable.star,
    containerColor = Yellow,
)


@Composable
fun SwipeWrapper(
    leftSwipeState: SwipeStateDefinition,
    rightSwipeState: SwipeStateDefinition,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val density = LocalDensity.current
    // 转换为像素 (PX)
    val shortPx = with(density) { SwipeActionThresholdDp.toPx() }     // 40dp
    val maxActionPx = with(density) { ActionTriggerMaxDp.toPx() }     // 120dp

    val haptic = LocalHapticFeedback.current
    var hasVibrated by remember { mutableStateOf(false) }

    // 2. 存储当前偏移量，使用 Animatable 允许动画回弹
    val offsetX = remember { Animatable(0f) }

    // 3. 定义拖动状态 (onDelta 负责实时更新位置)
    val draggableState = rememberDraggableState(onDelta = { delta ->
        coroutineScope.launch {
            // 限制向左滑动，并限制最大滑动距离（防止视图无限滑出）
            val newOffset = (offsetX.value + delta).coerceIn(-maxActionPx, maxActionPx)
            // 检查：1. 是否越过阈值 (> shortPx) 2. 是否是向右滑 3. 本次滑动是否未震动过
            if (newOffset.absoluteValue >= shortPx && offsetX.value.absoluteValue < shortPx && !hasVibrated) {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                hasVibrated = true // 标记已震动
            }
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
                hasVibrated = false
            }
        }
    }

    var heightPx by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        // 🌟 视觉反馈层：传入所有阈值
        SwipeActionsOverlay(
            currentOffset = offsetX.value,
            shortPx = shortPx,
            height = heightPx,
            leftSwipeState = leftSwipeState,
            rightSwipeState = rightSwipeState
        )

        Box(
            modifier = Modifier
                .onSizeChanged{
                    heightPx = it.height
                }
                .fillMaxWidth()
                // 🌟 应用 offset 使内容跟随手指滑动
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        val finalOffset = offsetX.value

                        if (finalOffset in shortPx..maxActionPx) {
                            Log.d("Swipe", "右短滑 for item")
                            rightSwipeState.onClick()
                        } else if (finalOffset < -shortPx && finalOffset >= -maxActionPx) {
                            Log.d("Swipe", "左滑触发: 另一个操作")
                            leftSwipeState.onClick()
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

data class SwipeStateDefinition(
    val icon: Int,
    val contentColor: Color = Color.White,
    val containerColor: Color,
    val idleContentColor: Color = Black50,
    val idleContainerColor: Color = Black08,
    val onClick: () -> Unit = {}
)


@SuppressLint("DefaultLocale")
@Composable
fun SwipeActionsOverlay(
    currentOffset: Float,
    shortPx: Float,
    height: Int,
    leftSwipeState: SwipeStateDefinition,
    rightSwipeState: SwipeStateDefinition,
) {
    // 2. 初始化 Icon 属性
    var icon: Int
    var iconColor: Color
    var containerColor: Color

    when {
        // 🌟 新增：左滑操作 (currentOffset < 0)
        currentOffset < -shortPx -> { // 阶段 B-Left: 触发区域
            icon = leftSwipeState.icon
            containerColor = leftSwipeState.containerColor
            iconColor = leftSwipeState.contentColor
        }
        currentOffset < 0f -> { // 阶段 A-Left: 渐显区域
            icon = leftSwipeState.icon
            containerColor = leftSwipeState.idleContainerColor
            iconColor = leftSwipeState.idleContentColor
        }
        // 阶段 B: 右短滑
        currentOffset > shortPx -> {
            icon = rightSwipeState.icon
            containerColor = rightSwipeState.containerColor
            iconColor = rightSwipeState.contentColor
        }
        // 阶段 A: 右短滑
        currentOffset > 0f -> {
            icon = rightSwipeState.icon
            containerColor = rightSwipeState.idleContainerColor
            iconColor = rightSwipeState.idleContentColor
        }
        else -> return // 不滑动，不渲染
    }

    val density = LocalDensity.current
    val dpValue = with(density) { currentOffset.toDp() }
    val targetHeightDp = with(density) { height.toDp() }
    // 5. 渲染操作区域
    Box(
        modifier = Modifier.height(targetHeightDp).fillMaxWidth(),
        contentAlignment = if (currentOffset > 0f) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = if (currentOffset > 0f) Alignment.Start else Alignment.End
        ) {
            val iconModifier = if (currentOffset > 0f) {
                Modifier.padding(start = 16.dp) // 右滑时在左侧留边距
            } else {
                Modifier.padding(end = 16.dp)   // 左滑时在右侧留边距
            }
            Box(
                modifier = iconModifier
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
            Text(String.format("%.2f", dpValue.value), modifier = iconModifier, style = AppTypography.M13)
        }
    }
}