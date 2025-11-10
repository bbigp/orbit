package cn.coolbet.orbit.ui.kit

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.Black50

@Composable
fun ProgressIndicator(
    id: Int = R.drawable.loading,
    size: Dp = 24.dp,
    color: Color = Black50,
) {

    // 1. 创建无限动画过渡
    val infiniteTransition = rememberInfiniteTransition(label = "RotationTransition")

    // 2. 定义旋转角度 (0度到360度)
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "RotationAnimation"
    )

    Image(
        painter = painterResource(id = id),
        contentDescription = "Loading",
        colorFilter = ColorFilter.tint(color),
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(size)
            .rotate(rotation) // 👈 应用旋转角度
    )
}