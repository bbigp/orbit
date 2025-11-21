package cn.coolbet.orbit.ui.kit

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * 模仿 iOS 风格的 Activity Indicator (Cupertino Spinner).
 * * 这是一个完全自定义的 Composable，不依赖任何实验性的或非官方的库。
 * 它使用 Canvas 和无限动画来模拟旋转和透明度变化。
 * * @param modifier 组件的 Modifier。
 * @param color 指示器的颜色。
 * @param size 指示器的总尺寸（即外接圆直径）。
 * @param segmentCount 指示器条目数量 (iOS 默认为 12)。
 * @param segmentLength 单个条目的长度。
 * @param segmentThickness 单个条目的粗细。
 * @param segmentGap 内部条目和条目间的间隙。
 */
@Composable
fun CupertinoActivityIndicator(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFE3E0E0),
    size: Dp = 20.dp,
    segmentCount: Int = 8,
    segmentLength: Dp = 6.67.dp,
    segmentThickness: Dp = 2.22.dp,
    segmentGap: Dp = 10.dp // 条目与中心点之间的距离
) {
    // 1. 无限旋转动画
    val infiniteTransition = rememberInfiniteTransition(label = "SpinnerTransition")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = segmentCount.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "SpinnerAngle"
    )

    // 计算当前动画帧对应的起始索引（0到segmentCount-1）
    val startIndex = angle.toInt() % segmentCount

    // 2. Canvas 绘制
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.toPx() / 2, size.toPx() / 2)
            val radius = size.toPx() / 2

            // 绘制每个条目
            for (i in 0 until segmentCount) {
                // 计算当前条目的透明度
                // 索引越靠近 startIndex，透明度越高
                val indexDiff = (i - startIndex + segmentCount) % segmentCount
                // 使用平方或指数函数模拟平滑的透明度衰减
                val base = indexDiff.toFloat() / segmentCount
                // 🚀 FIX: 直接使用 top-level kotlin.math.pow(base, exponent) 函数
                val opacity = 1f - base.pow(2)

                // 计算角度（弧度）
                val angleRad = 2 * Math.PI * i / segmentCount

                // 确定条目的起始和结束点
                val startX = center.x + (radius - segmentLength.toPx() - segmentGap.toPx()) * cos(angleRad).toFloat()
                val startY = center.y + (radius - segmentLength.toPx() - segmentGap.toPx()) * sin(angleRad).toFloat()

                val endX = center.x + (radius - segmentGap.toPx()) * cos(angleRad).toFloat()
                val endY = center.y + (radius - segmentGap.toPx()) * sin(angleRad).toFloat()

                drawLine(
                    color = color.copy(alpha = opacity),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = segmentThickness.toPx(),
                    cap = StrokeCap.Round // 圆头
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewIOSIndicator() {
    // 在预览中测试指示器
    Box(modifier = Modifier.padding(20.dp)) {
        CupertinoActivityIndicator(
            size = 48.dp
        )
    }
}