package cn.coolbet.orbit.ui.kit

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.ObTheme
import kotlinx.coroutines.delay

@SuppressLint("RememberReturnType")
@Composable
fun ListTileChevronUpDown(
    modifier: Modifier = Modifier,
    title: String,
    trailing: String,
    icon: Int,
    menuContent: @Composable () -> Unit = {},
    showMenu: Boolean = false
) {
    val expandedState = remember { MutableTransitionState(showMenu) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(ObTheme.colors.primaryContainer)
            .click { expandedState.targetState = !expandedState.targetState }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(ObTheme.colors.secondary),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTypography.R15,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp)) // 12 + 4

        // --- 关键区域：局部 Box 锚点 ---
        Box(
            // 关键：在这里测量 Box 的尺寸
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentSize(Alignment.TopEnd)
        ) {
            // 1. 锚点内容（右侧的 Text 和 Image）
            Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                Text(trailing, maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R15B50)
                Spacer(modifier = Modifier.width(4.dp)) //4 + 4
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.chevron_up_down),
                    contentDescription = "",
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(ObTheme.colors.tertiary),
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            ObDropdownMenu(
                expandedState = expandedState,
            ) {
                Column {
                    menuContent()
                }
            }
        }
    }
}
