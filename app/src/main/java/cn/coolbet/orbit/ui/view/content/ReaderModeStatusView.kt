package cn.coolbet.orbit.ui.view.content

import android.os.SystemClock
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObTextButton
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.Black50
import cn.coolbet.orbit.ui.theme.ContainerRed
import cn.coolbet.orbit.ui.theme.ContentRed
import cn.coolbet.orbit.ui.view.listdetail.component.skeleton.shimmer

@Composable
fun ReaderModeLoadingSkeleton(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .shimmer()
        )
        Spacer(modifier = Modifier.height(20.dp))
        SkeletonLine(widthFraction = 0.86f, height = 18.dp, radius = 6.dp)
        Spacer(modifier = Modifier.height(10.dp))
        SkeletonLine(widthFraction = 0.52f, height = 14.dp, radius = 6.dp)
        Spacer(modifier = Modifier.height(22.dp))
        SkeletonLine(widthFraction = 1f, height = 12.dp)
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonLine(widthFraction = 1f, height = 12.dp)
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonLine(widthFraction = 0.92f, height = 12.dp)
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonLine(widthFraction = 0.96f, height = 12.dp)
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonLine(widthFraction = 1f, height = 12.dp)
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonLine(widthFraction = 0.88f, height = 12.dp)
    }
}

@Composable
fun ReaderModeFailure(
    onRetry: () -> Unit,
    onExitReaderMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(ContainerRed),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ban),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ContentRed),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "阅读模式不可用",
            style = AppTypography.M22,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "当前页面解析失败，你可以重试解析，或切回原始网页。",
            style = AppTypography.R15B50,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        ObTextButton(
            content = "重试解析",
            onClick = onRetry
        )
        Spacer(modifier = Modifier.height(12.dp))
        ObTextButton(
            content = "查看原网页",
            colors = OButtonDefaults.stroked.copy(borderColor = Black08, contentColor = Black50),
            onClick = onExitReaderMode
        )
    }
}

@Composable
fun WebRenderLoadingOverlay(
    visible: Boolean,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    var showBackground by remember { mutableStateOf(visible) }
    var showSkeleton by remember { mutableStateOf(false) }
    var skeletonShownAtMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(visible) {
        if (visible) {
            showBackground = true
            showSkeleton = false
            skeletonShownAtMs = 0L
            kotlinx.coroutines.delay(200L)
            if (visible) {
                showSkeleton = true
                skeletonShownAtMs = SystemClock.elapsedRealtime()
            }
        } else {
            if (showSkeleton) {
                val elapsed = SystemClock.elapsedRealtime() - skeletonShownAtMs
                val remain = (600L - elapsed).coerceAtLeast(0L)
                if (remain > 0L) {
                    kotlinx.coroutines.delay(remain)
                }
                showSkeleton = false
                skeletonShownAtMs = 0L
            }
            showBackground = false
        }
    }

    val backgroundAlpha = animateFloatAsState(
        targetValue = if (showBackground) 1f else 0f,
        animationSpec = tween(durationMillis = if (showBackground) 0 else 200),
        label = "web-render-overlay-background-alpha"
    ).value
    val skeletonAlpha = animateFloatAsState(
        targetValue = if (showSkeleton) 1f else 0f,
        animationSpec = tween(durationMillis = if (showSkeleton) 180 else 200),
        label = "web-render-overlay-skeleton-alpha"
    ).value

    if (backgroundAlpha <= 0.01f && skeletonAlpha <= 0.01f) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(backgroundAlpha)
            .background(backgroundColor)
    ) {
        Box(modifier = Modifier.alpha(skeletonAlpha)) {
            ReaderModeLoadingSkeleton()
        }
    }
}

@Composable
private fun SkeletonLine(
    widthFraction: Float,
    height: androidx.compose.ui.unit.Dp,
    radius: androidx.compose.ui.unit.Dp = 4.dp,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(radius))
            .shimmer()
    )
}
