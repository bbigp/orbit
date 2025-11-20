package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.ObTheme


@Composable
fun SpacerDivider(
    thickness: Dp = 0.5.dp, color: Color = Black08,
    start: Dp = 0.dp, end: Dp = 0.dp,
    background: Color = ObTheme.colors.primaryContainer,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(thickness)
            .background(background)
    ) {
        if (start > 0.dp) {
            Spacer(modifier = Modifier.width(start))
        }
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = thickness, color = color)
        if (end > 0.dp) {
            Spacer(modifier = Modifier.width(end))
        }
    }
}

@Composable
fun DashedDivider(
    color: Color = Black08,
    thickness: Dp = 1.dp, // 分割线粗细 (strokeWidth)
    spacing: Dp = 16.dp, // 空白模块总高度 (Divider 的总高度)
    indent: Dp = 0.dp, // 分割线左右边距
    dashLength: Dp = 5.dp,
    dashSpacing: Dp = 5.dp,
) {
    // 1. 设置整体尺寸和边距
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(spacing)
            .padding(horizontal = indent)
    ) {
        // 2. 使用 Canvas 绘制内容
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerY = canvasHeight / 2f

            // 转换为像素 (Float)
            val strokePx = thickness.toPx()
            val dashLengthPx = dashLength.toPx()
            val dashSpacingPx = dashSpacing.toPx()

            // 使用 PathEffect 绘制虚线，这是 Compose 中绘制虚线的标准方法
            val pathEffect = PathEffect.dashPathEffect(
                // intervals: [dash length, gap length]
                intervals = floatArrayOf(dashLengthPx, dashSpacingPx),
                phase = 0f // 虚线的起始偏移
            )

            // 绘制一条覆盖整个 Canvas 宽度的线
            drawLine(
                color = color,
                start = Offset(0f, centerY),
                end = Offset(canvasWidth, centerY),
                strokeWidth = strokePx,
                cap = StrokeCap.Round, // 对应 Flutter 的 StrokeCap.round
                pathEffect = pathEffect
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSpacerDivider() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        Column {
            Spacer(modifier = Modifier.height(10.dp))
            SpacerDivider(start = 40.dp, end = 40.dp)
            Spacer(modifier = Modifier.height(10.dp))
            DashedDivider()
        }
    }
}