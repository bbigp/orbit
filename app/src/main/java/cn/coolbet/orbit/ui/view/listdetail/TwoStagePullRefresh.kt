package cn.coolbet.orbit.ui.view.listdetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.roundToInt

private enum class RefreshingIndicatorType { Circle, Snowflake }

/**
 * Two-stage pull-to-refresh aligned with Brew iOS behavior:
 * - Stage 1 (progress < 1.15): two balls
 * - Stage 2 (progress >= 1.15): rotating snowflake
 *
 * Trigger on release:
 * - progress >= secondStageTrigger and secondStageAction != null -> secondStageAction()
 * - progress >= refreshTrigger -> onRefresh()
 */
@Composable
fun TwoStagePullRefreshLayout(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    secondStageAction: (() -> Unit)?,
    canPullDown: () -> Boolean,
    modifier: Modifier = Modifier,
    threshold: Dp = 56.dp,
    refreshTrigger: Float = 0.6f,
    secondStageTrigger: Float = 1.5f,
    pullResistance: Float = 1.3f,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current

    val thresholdPx = with(density) { threshold.toPx() }
    val maxPullPx = thresholdPx * 2.6f
    var offsetTargetY by remember { mutableFloatStateOf(0f) }
    val offsetY by animateFloatAsState(
        targetValue = offsetTargetY,
        animationSpec = spring(),
        label = "two_stage_pull_offset"
    )

    var progress by remember { mutableFloatStateOf(0f) }
    var crossedStage1 by remember { mutableStateOf(false) }
    var crossedStage2 by remember { mutableStateOf(false) }
    var pullingByUser by remember { mutableStateOf(false) }
    var refreshingIndicatorType by remember { mutableStateOf(RefreshingIndicatorType.Circle) }

    fun updateProgress() {
        progress = (offsetY / thresholdPx).coerceAtLeast(0f)
        if (pullingByUser && progress >= refreshTrigger && !crossedStage1) {
            crossedStage1 = true
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        } else if (progress < refreshTrigger) {
            crossedStage1 = false
        }

        if (pullingByUser && progress >= secondStageTrigger && !crossedStage2) {
            crossedStage2 = true
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        } else if (progress < secondStageTrigger) {
            crossedStage2 = false
        }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            offsetTargetY = thresholdPx
        } else {
            offsetTargetY = 0f
            refreshingIndicatorType = RefreshingIndicatorType.Circle
        }
        updateProgress()
    }

    val nestedScroll = remember(canPullDown, isRefreshing, secondStageAction) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput) return Offset.Zero
                if (available.y < 0f && offsetY > 0f) {
                    val next = (offsetY + available.y).coerceAtLeast(0f)
                    val consumedY = next - offsetY
                    offsetTargetY = next
                    return Offset(0f, consumedY)
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput) return Offset.Zero
                if (isRefreshing) return Offset.Zero
                if (available.y > 0f && canPullDown()) {
                    pullingByUser = true
                    val delta = available.y * pullResistance
                    val next = (offsetY + delta).coerceAtMost(maxPullPx)
                    val consumedY = next - offsetY
                    offsetTargetY = next
                    return Offset(0f, consumedY)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                pullingByUser = false
                updateProgress()

                if (!isRefreshing && progress >= secondStageTrigger && secondStageAction != null) {
                    refreshingIndicatorType = RefreshingIndicatorType.Snowflake
                    secondStageAction()
                    offsetTargetY = 0f
                    updateProgress()
                    return Velocity.Zero
                }

                if (!isRefreshing && progress >= refreshTrigger) {
                    refreshingIndicatorType = RefreshingIndicatorType.Circle
                    onRefresh()
                    offsetTargetY = thresholdPx
                    updateProgress()
                    return Velocity.Zero
                }

                offsetTargetY = 0f
                updateProgress()
                return Velocity.Zero
            }
        }
    }

    LaunchedEffect(offsetY) { updateProgress() }

    Box(modifier = modifier.nestedScroll(nestedScroll)) {
        Box(modifier = Modifier.offset { IntOffset(0, offsetY.roundToInt()) }) {
            content()
        }

        val showSecondStage = secondStageAction != null && progress >= 1.15f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(threshold)
                .offset { IntOffset(0, (offsetY - thresholdPx).roundToInt()) },
            contentAlignment = Alignment.Center
        ) {
            when {
                isRefreshing && refreshingIndicatorType == RefreshingIndicatorType.Snowflake -> RefreshingSnowflakeIndicator()
                isRefreshing -> CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                pullingByUser && showSecondStage -> SnowflakeIndicator(
                    progress = progress,
                    secondStageStart = 1.15f,
                    secondStageTrigger = secondStageTrigger
                )
                pullingByUser && progress > 0f -> TwoBallIndicator(progress = progress.coerceIn(0f, 1f))
            }
        }
    }
}



@Composable
private fun TwoBallIndicator(progress: Float) {
    Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset(x = (-10f * progress).dp, y = (10f * progress).dp)
                .size(14.dp)
                .background(Color.Black, CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = (5f * progress).dp, y = (-5f * progress).dp)
                .size(24.dp)
                .background(Color.Black, CircleShape)
        )
    }
}

@Composable
private fun RefreshingSnowflakeIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "refresh_snowflake")
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refresh_snowflake_rotation"
    )

    Icon(
        imageVector = Icons.Filled.Face,
        contentDescription = "Refreshing",
        tint = Color.Black,
        modifier = Modifier.size(28.dp).rotate(rotation.value)
    )
}

@Composable
private fun SnowflakeIndicator(
    progress: Float,
    secondStageStart: Float,
    secondStageTrigger: Float
) {
    // Make stage-2 growth more obvious: stronger easing + larger max size.
    val trigger = if (secondStageTrigger > secondStageStart) secondStageTrigger else secondStageStart + 0.01f
    val stageProgress = ((progress - secondStageStart) / (trigger - secondStageStart)).coerceIn(0f, 1f)
    val eased = stageProgress * 0.75f + (1f - (1f - stageProgress) * (1f - stageProgress)) * 0.25f
    val minSize = 15f
    val maxSize = 28f
    val size = (minSize + (maxSize - minSize) * eased).dp
    val rotation = (progress * 100f * PI).toFloat()

    Icon(
        imageVector = Icons.Filled.Face,
        contentDescription = "Snowflake",
        tint = Color.Black,
        modifier = Modifier.size(size).rotate(rotation)
    )
}
