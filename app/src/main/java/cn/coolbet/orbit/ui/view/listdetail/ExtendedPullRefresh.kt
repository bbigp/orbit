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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableLongStateOf
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
private const val SHORT_PULL_BIT = 0
private const val LONG_PULL_BIT = 1

/**
 * Pull-to-refresh with short-pull and long-pull behavior:
 * - Short pull (progress < 1.15): two balls
 * - Long pull (progress >= 1.15): rotating snowflake
 *
 * Trigger on release:
 * - progress >= longPullTrigger and onLongPull != null -> onLongPull()
 * - progress >= shortPullTrigger -> onRefresh() ?: onShortPull()
 */
@Composable
fun ExtendedPullRefreshLayout(
    isRefreshing: Boolean,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onShortPull: (() -> Unit)? = null,
    onLongPull: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
    threshold: Dp = 72.dp,
    shortPullIndicatorStart: Float = 0.08f,
    shortPullTrigger: Float = 1.0f,
    longPullTrigger: Float = 1.85f,
    longPullIndicatorStart: Float = 1.6f,
    pullResistance: Float = 0.58f,
    content: @Composable () -> Unit
) {
    val shortPullAction = onRefresh ?: onShortPull
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current
    fun canPullDown(): Boolean =
        listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0

    val thresholdPx = with(density) { threshold.toPx() }
    val maxPullPx = thresholdPx * 2.6f
    var offsetTargetY by remember { mutableFloatStateOf(0f) }
    val offsetY by animateFloatAsState(
        targetValue = offsetTargetY,
        animationSpec = spring(),
        label = "extended_pull_offset"
    )

    var progress by remember { mutableFloatStateOf(0f) }
    var pullFlags by remember { mutableLongStateOf(0L) }
    var pullingByUser by remember { mutableStateOf(false) }
    var refreshingIndicatorType by remember { mutableStateOf(RefreshingIndicatorType.Circle) }

    fun isPast(bit: Int): Boolean = (pullFlags and (1L shl bit)) != 0L
    fun setPast(bit: Int, value: Boolean) {
        pullFlags = if (value) {
            pullFlags or (1L shl bit)
        } else {
            pullFlags and (1L shl bit).inv()
        }
    }

    fun updateStageHaptic(bit: Int, isPastThreshold: Boolean) {
        val crossedNow = pullingByUser && isPastThreshold && !isPast(bit)
        if (crossedNow) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        setPast(bit, isPastThreshold)
    }

    fun updateProgress(currentOffsetY: Float) {
        val nextProgress = (currentOffsetY / thresholdPx).coerceAtLeast(0f)
        progress = nextProgress

        updateStageHaptic(bit = SHORT_PULL_BIT, isPastThreshold = nextProgress >= shortPullTrigger)
        updateStageHaptic(bit = LONG_PULL_BIT, isPastThreshold = nextProgress >= longPullTrigger)
    }

    fun setOffsetTarget(next: Float) {
        offsetTargetY = next
        updateProgress(currentOffsetY = offsetTargetY)
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            setOffsetTarget(thresholdPx)
        } else {
            setOffsetTarget(0f)
            refreshingIndicatorType = RefreshingIndicatorType.Circle
        }
    }

    val nestedScroll = remember(listState, isRefreshing, onLongPull) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput) return Offset.Zero
                if (available.y < 0f && offsetTargetY > 0f) {
                    val next = (offsetTargetY + available.y).coerceAtLeast(0f)
                    val consumedY = next - offsetTargetY
                    setOffsetTarget(next)
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
                    val next = (offsetTargetY + delta).coerceAtMost(maxPullPx)
                    val consumedY = next - offsetTargetY
                    setOffsetTarget(next)
                    return Offset(0f, consumedY)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                pullingByUser = false
                updateProgress(currentOffsetY = offsetY)

                if (!isRefreshing && progress >= longPullTrigger && onLongPull != null) {
                    refreshingIndicatorType = RefreshingIndicatorType.Snowflake
                    onLongPull()
                    setOffsetTarget(0f)
                    return Velocity.Zero
                }

                if (!isRefreshing && progress >= shortPullTrigger && shortPullAction != null) {
                    refreshingIndicatorType = RefreshingIndicatorType.Circle
                    shortPullAction()
                    setOffsetTarget(thresholdPx)
                    return Velocity.Zero
                }

                setOffsetTarget(0f)
                return Velocity.Zero
            }
        }
    }

    LaunchedEffect(offsetY) { updateProgress(currentOffsetY = offsetY) }

    Box(modifier = modifier.fillMaxSize().nestedScroll(nestedScroll)) {
        Box(modifier = Modifier.offset { IntOffset(0, offsetY.roundToInt()) }) {
            content()
        }

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
                pullingByUser && onLongPull != null && progress >= longPullIndicatorStart -> SnowflakeIndicator(
                    progress = progress,
                    longPullIndicatorStart = longPullIndicatorStart,
                    longPullTrigger = longPullTrigger
                )
                pullingByUser && progress >= shortPullIndicatorStart -> TwoBallIndicator(
                    progress = progress.coerceIn(0f, 1f)
                )
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
    longPullIndicatorStart: Float,
    longPullTrigger: Float
) {
    // Make long-pull growth more obvious: stronger easing + larger max size.
    val trigger = if (longPullTrigger > longPullIndicatorStart) longPullTrigger else longPullIndicatorStart + 0.01f
    val longPullProgress = ((progress - longPullIndicatorStart) / (trigger - longPullIndicatorStart)).coerceIn(0f, 1f)
    val eased = longPullProgress * 0.75f + (1f - (1f - longPullProgress) * (1f - longPullProgress)) * 0.25f
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
