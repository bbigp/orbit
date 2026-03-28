package cn.coolbet.orbit.ui.view.content

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
