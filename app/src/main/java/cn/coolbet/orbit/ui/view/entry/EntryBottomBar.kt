package cn.coolbet.orbit.ui.view.entry

import android.content.Intent
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.core.net.toUri
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.copyText
import cn.coolbet.orbit.common.openURL
import cn.coolbet.orbit.common.shareText
import cn.coolbet.orbit.ui.kit.ObDropdownMenu
import cn.coolbet.orbit.ui.kit.ObDropdownMenuItem
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.ObTheme
import kotlinx.coroutines.delay

@Composable
fun EntryBottomBar(
    state: EntryState
) {
    val changeReaderView = LocalChangeReaderView.current
    val expandedState = remember { MutableTransitionState(false) }
    var isClickDisabled by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(expandedState.targetState) {
        if (!expandedState.targetState) {
            isClickDisabled = true
            delay(100) // 2. 引入一个短暂延迟，以确保 Row 的 clickable 在状态改变后立即执行时被忽略
            isClickDisabled = false
        }
    }

    Column(
        modifier =
            Modifier.fillMaxWidth()
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .background(ObTheme.colors.primaryContainer)
    ) {
        SpacerDivider(thickness = 1.dp)
        Row(
            modifier = Modifier.height(48.dp)
                .padding(start = 20.dp, end = 20.dp, bottom = 8.dp, top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ObIcon(
                id = R.drawable.arrow_left,
                modifier = Modifier.clickable(onClick = { NavigatorBus.pop() }),
            )
            ObIcon(id = R.drawable.check_o)
            ObIcon(id = R.drawable.star)
            ObIcon(
                id = if (state.readerView) R.drawable.book else R.drawable.page,
                modifier = Modifier.clickable(onClick = { changeReaderView() })
            )
            ObIcon(id = R.drawable.chevron_down)
            Box{
                ObIcon(
                    id = R.drawable.more,
                    modifier = Modifier.clickable(
                        enabled = !isClickDisabled,
                        onClick = { expandedState.targetState = !expandedState.targetState }
                    )
                )
                ObDropdownMenu (
                    onDismissRequest = { expandedState.targetState = false },
                    expandedState = expandedState,
                ) {
                    ObDropdownMenuItem(
                        text = "使用浏览器打开",
                        leadingIcon = R.drawable.explorer,
                        onClick = {
                            openURL(context, state.entry.url.toUri())
                            expandedState.targetState = false
                        }
                    )
                    ObDropdownMenuItem(
                        text = "分享",
                        leadingIcon = R.drawable.share,
                        onClick = {
                            shareText(context, "${state.entry.title} \n ${state.entry.url}", state.entry.title)
                            expandedState.targetState = false
                        }
                    )
                    ObDropdownMenuItem(
                        text = "复制链接",
                        leadingIcon = R.drawable.link,
                        onClick = {
                            copyText(context, state.entry.url)
                            expandedState.targetState = false
                        }
                    )
                    ObDropdownMenuItem(
                        text = "阅读设置",
                        leadingIcon = R.drawable.brush
                    )
                }
            }

        }
    }

}