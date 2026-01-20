package cn.coolbet.orbit.ui.view.list_detail.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.kit.DashedDivider
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Skeleton


@Preview(showBackground = true)
@Composable
fun EntriesSkeleton() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) {
        item(key = "entry-skeleton-top") {
            Column(modifier = Modifier.padding(horizontal = 14.dp)) {
            Spacer(modifier = Modifier.height(8.dp).fillMaxWidth())
            Text("All", style = AppTypography.M28.copy(color = Skeleton))
//            Spacer(modifier = Modifier.height(4.dp).fillMaxWidth())
            Spacer(modifier = Modifier
                .padding(top = 2.dp)
                .width(100.dp)
                .height(13.dp)
                .clip(RoundedCornerShape(3.dp))
                .shimmer()
            )
            Spacer(modifier = Modifier.height(16.dp).fillMaxWidth())
            DashedDivider(indent = 2.dp)
        } }
        items(20) {
            LDMagazineSkeleton()
            Box(modifier = Modifier.padding(horizontal = 16.dp)) { SpacerDivider() }
        }
    }
}

@Composable
fun LDHeaderSkeleton() {
    Column(modifier = Modifier.padding(horizontal = 14.dp)) {
        Spacer(modifier = Modifier.height(8.dp).fillMaxWidth())
        Text("All", style = AppTypography.M28.copy(color = Skeleton))
        Spacer(modifier = Modifier.height(4.dp).fillMaxWidth())
        Spacer(modifier = Modifier
            .padding(top = 2.dp)
            .width(100.dp)
            .height(13.dp)
            .clip(RoundedCornerShape(3.dp))
            .shimmer()
        )
        Spacer(modifier = Modifier.height(16.dp).fillMaxWidth())
        DashedDivider(indent = 2.dp)
    }
}

fun Modifier.shimmer(
    baseColor: Color = Skeleton.copy(alpha = 0.6f), // 骨架屏的基础颜色
    highlightColor: Color = Skeleton.copy(alpha = 0.9f), // 闪光部分的颜色
    shimmerWidth: Dp = 200.dp, // 闪光区域的宽度
    durationMillis: Int = 1000, // 动画持续时间
    delayMillis: Int = 200, // 延迟开始时间
    shape: Shape = RoundedCornerShape(4.dp) // 骨架屏的形状 (可选)
): Modifier = composed {
    val density = LocalDensity.current
    var size by remember { mutableStateOf(IntSize.Zero) }

    val transition = rememberInfiniteTransition(label = "ShimmerAnimation")
    val xShimmer by transition.animateFloat(
        initialValue = -1f, // 从左边完全移出开始
        targetValue = 1f,   // 到右边完全移出结束
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
                delayMillis = delayMillis
            ),
            repeatMode = RepeatMode.Restart
        ), label = "ShimmerOffsetX"
    )

    val (gradientStart, gradientEnd) = with(density) {
        // 将 Dp 转换为 Px
        val shimmerWidthPx = shimmerWidth.toPx()

        // 计算渐变色刷子的位置
        // xShimmer 在 [-1f, 1f] 之间变化
        // 我们需要考虑 Composable 的实际宽度 size.width
        val width = size.width.toFloat()

        // gradientStart/End 决定了渐变刷子从左到右滑动的范围
        val startX = xShimmer * (shimmerWidthPx + width) / 2
        val endX = startX + shimmerWidthPx

        Pair(startX, endX)
    }

    // 创建线性渐变刷子
    val brush = Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor, // 闪光部分
            baseColor
        ),
        start = Offset(gradientStart, 0f),
        end = Offset(gradientEnd, 0f)
    )

    this
        .onSizeChanged { size = it }
        .background(brush = brush, shape = shape)
}