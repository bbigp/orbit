package cn.coolbet.orbit.ui.view.listdetail.component

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private val SwipeActionTriggerThresholdDp = 38.dp
private val SwipeActionMaxOffsetDp = 100.dp

val MarkUnreadSwipeAction = SwipeActionSpec(
    icon = R.drawable.unread,
    containerColor = Green,
)

val MarkReadSwipeAction = SwipeActionSpec(
    icon = R.drawable.check_o,
    containerColor = Green,
)

val DisabledSwipeAction = SwipeActionSpec(
    icon = R.drawable.ban,
    containerColor = Blue,
)

val OpenBrowserSwipeAction = SwipeActionSpec(
    icon = R.drawable.out_o,
    containerColor = Purple,
)

val AddCollectionSwipeAction = SwipeActionSpec(
    icon = R.drawable.star,
    containerColor = Yellow,
)

data class SwipeActionSpec(
    val icon: Int,
    val contentColor: Color = Color.White,
    val containerColor: Color,
    val idleContentColor: Color = Black50,
    val idleContainerColor: Color = Black08,
    val onTrigger: () -> Unit = {}
)

@Composable
fun SwipeActionItem(
    startAction: SwipeActionSpec,
    endAction: SwipeActionSpec,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val triggerThresholdPx = with(density) { SwipeActionTriggerThresholdDp.toPx() }
    val maxOffsetPx = with(density) { SwipeActionMaxOffsetDp.toPx() }
    val haptic = LocalHapticFeedback.current
    val currentStartAction by rememberUpdatedState(startAction)
    val currentEndAction by rememberUpdatedState(endAction)

    var hasTriggeredHaptic by remember { mutableStateOf(false) }
    var heightPx by remember { mutableIntStateOf(0) }
    val offsetX = remember { Animatable(0f) }

    fun updateOffset(delta: Float) {
        coroutineScope.launch {
            val nextOffset = (offsetX.value + delta).coerceIn(-maxOffsetPx, maxOffsetPx)
            val passedThreshold = nextOffset.absoluteValue >= triggerThresholdPx &&
                offsetX.value.absoluteValue < triggerThresholdPx
            if (passedThreshold && !hasTriggeredHaptic) {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                hasTriggeredHaptic = true
            }
            offsetX.snapTo(nextOffset)
        }
    }

    fun animateBack() {
        coroutineScope.launch {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300)
            )
            hasTriggeredHaptic = false
        }
    }

    fun triggerActionIfNeeded() {
        when {
            offsetX.value >= triggerThresholdPx -> currentEndAction.onTrigger()
            offsetX.value <= -triggerThresholdPx -> currentStartAction.onTrigger()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        SwipeActionOverlay(
            currentOffset = offsetX.value,
            triggerThresholdPx = triggerThresholdPx,
            heightPx = heightPx,
            startAction = startAction,
            endAction = endAction
        )

        Box(
            modifier = Modifier
                .onSizeChanged { heightPx = it.height }
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(triggerThresholdPx, maxOffsetPx) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            updateOffset(dragAmount)
                        },
                        onDragEnd = {
                            triggerActionIfNeeded()
                            animateBack()
                        },
                        onDragCancel = {
                            animateBack()
                        }
                    )
                }
        ) {
            content()
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun SwipeActionOverlay(
    currentOffset: Float,
    triggerThresholdPx: Float,
    heightPx: Int,
    startAction: SwipeActionSpec,
    endAction: SwipeActionSpec,
) {
    val actionSpec = when {
        currentOffset < -triggerThresholdPx -> startAction.activeVisual()
        currentOffset < 0f -> startAction.idleVisual()
        currentOffset > triggerThresholdPx -> endAction.activeVisual()
        currentOffset > 0f -> endAction.idleVisual()
        else -> null
    } ?: return

    val density = LocalDensity.current
    val offsetDp = with(density) { currentOffset.toDp() }
    val heightDp = with(density) { heightPx.toDp() }
    val showOnStart = currentOffset > 0f

    Box(
        modifier = Modifier
            .height(heightDp)
            .fillMaxWidth(),
        contentAlignment = if (showOnStart) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = if (showOnStart) Alignment.Start else Alignment.End
        ) {
            val iconModifier = if (showOnStart) {
                Modifier.padding(start = 16.dp)
            } else {
                Modifier.padding(end = 16.dp)
            }
            Box(
                modifier = iconModifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(actionSpec.containerColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = actionSpec.icon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(actionSpec.contentColor),
                )
            }
            Text(
                text = String.format("%.2f", offsetDp.value),
                modifier = iconModifier,
                style = AppTypography.M13
            )
        }
    }
}

private data class SwipeActionVisual(
    val icon: Int,
    val containerColor: Color,
    val contentColor: Color,
)

private fun SwipeActionSpec.idleVisual(): SwipeActionVisual {
    return SwipeActionVisual(
        icon = icon,
        containerColor = idleContainerColor,
        contentColor = idleContentColor
    )
}

private fun SwipeActionSpec.activeVisual(): SwipeActionVisual {
    return SwipeActionVisual(
        icon = icon,
        containerColor = containerColor,
        contentColor = contentColor
    )
}
