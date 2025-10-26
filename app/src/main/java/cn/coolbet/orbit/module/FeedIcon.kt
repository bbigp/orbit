package cn.coolbet.orbit.module

import android.util.Log
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.ElementSize
import cn.coolbet.orbit.ui.theme.M11White00
import cn.coolbet.orbit.ui.theme.M15White00
import coil3.compose.SubcomposeAsyncImage


enum class FeedIconSize (val size: Dp, val radius: Dp, val style: TextStyle) {
    SMALL( size = 18.dp, radius = 4.dp, style = M11White00),
    MEDIUM( size = 24.dp, radius = 6.dp, style = M15White00),
    LARGE( size = 36.dp, radius = 8.dp, style = M15White00);
    companion object {
        fun get(elementSize: ElementSize): FeedIconSize {
            return when (elementSize) {
                ElementSize.SMALL -> SMALL
                ElementSize.LARGE -> LARGE
                else -> MEDIUM
            }
        }
    }
}


@Composable
fun FeedIcon(url: String, alt: String, size: ElementSize = ElementSize.MEDIUM) {
    val iconSize = FeedIconSize.get(size)
    Box(modifier = Modifier
        .size(iconSize.size)
        .clip(RoundedCornerShape(iconSize.radius)),
        contentAlignment = Alignment.Center) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = alt,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            onError = {state ->
                Log.e("ImageLoadError", "Error: ${state.result.throwable.localizedMessage}")
            },
            loading = {
                ShimmerContainer(size = iconSize.size)
            },
            error = {
                val initial = alt.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
                Box(modifier = Modifier.fillMaxSize()
                    .background(
                        Brush.verticalGradient(colors = listOf(Color(0x80555555), Color(0xBF555555))
                    )),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = initial,
                        style = iconSize.style,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                    )
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFeedIcon(){
    FeedIcon(url = "https://cdn-static.sspai.com/favicon/sspai.ico", alt = "少数派")
}


@Composable
fun ShimmerContainer(
    size: Dp,
    baseColor: Color = Color(0xFFE0E0E0),
    highlightColor: Color = Color(0xFFF5F5F5)
) {
    Box(
        modifier = Modifier
            .size(size) // 设置尺寸
            // 关键修正：在应用 shimmer 效果之前，确保内容被裁剪到边界内
            .clipToBounds()
            .shimmer(baseColor = baseColor, highlightColor = highlightColor)
    )
}



fun Modifier.shimmer(
    baseColor: Color,
    highlightColor: Color,
    animationDurationMillis: Int = 1200,
    shimmerWidthRatio: Float = 0.8f
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmerTransition")

    val xShimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -1.0f,
        targetValue = 1.0f + shimmerWidthRatio,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = animationDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "xShimmerTranslate"
    )

    // 修正：我们不在这里调用 clip()。
    // 我们将渐变作为背景应用，并让外部的 Modifier 负责裁剪。
    this.background(Color.White) // 首先给一个基础背景色 (可选)
        .drawWithCache {
            // 使用 drawWithCache 更精确地控制绘制，避免 graphicsLayer 导致的其他副作用
            val size = this.size
            onDrawBehind {
                val brush = Brush.linearGradient(
                    colors = listOf(baseColor, highlightColor, baseColor),
                    start = Offset(xShimmerTranslate * size.width, 0f),
                    end = Offset((xShimmerTranslate + shimmerWidthRatio) * size.width, size.height)
                )
                // 绘制渐变，它会被 Box 的 clip/clipToBounds 裁剪
                drawRect(brush = brush)
            }
        }
}