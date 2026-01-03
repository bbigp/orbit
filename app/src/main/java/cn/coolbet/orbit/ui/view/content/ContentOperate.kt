package cn.coolbet.orbit.ui.view.content

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.common.copyText
import cn.coolbet.orbit.common.openURL
import cn.coolbet.orbit.common.shareText
import cn.coolbet.orbit.ui.kit.ObDropdownMenu
import cn.coolbet.orbit.ui.kit.ObDropdownMenuItem
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.ObToastManager
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.ObTheme
import cn.coolbet.orbit.ui.view.content.setting.ContentDisplaySettingSheet

@Composable
fun ContentOperate(
    state: ContentState,
    queryContext: QueryContext
) {
    val changeReaderView = LocalChangeReaderView.current
    val changeStarred = LocalChangeStarred.current
    val nextEntry = LocalNextEntry.current
    val expandedState = remember { MutableTransitionState(false) }
    val context = LocalContext.current
    val density = LocalDensity.current
    var showDisplaySettingSheet by remember { mutableStateOf(false) }

    ContentDisplaySettingSheet(
        showDisplaySettingSheet,
        onDismiss = { showDisplaySettingSheet = false }
    )

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
                modifier = Modifier.clickable { NavigatorBus.pop() },
            )
            ObIcon(
                id = if (state.entry.starred) R.drawable.star_fill else R.drawable.star,
                modifier = Modifier.clickable {
                    val msg = if (state.entry.starred) "Removed from Starred" else "Added to Starred"
                    changeStarred()
                    ObToastManager.show(msg)
                }
            )
            ObIcon(
                id = if (state.readerView) R.drawable.book else R.drawable.page,
                modifier = Modifier.clickable { changeReaderView() }
            )
            ObIcon(
                id = R.drawable.chevron_down,
                modifier = Modifier.clickable {
                    val entry = nextEntry()
                    if (entry == null)return@clickable
                    NavigatorBus.replace(Route.Entry(entry, queryContext))
                }
            )
            Box{
                ObIcon(
                    id = R.drawable.more,
                    modifier = Modifier.clickable {
                        expandedState.targetState = !expandedState.targetState
                    }
                )
                var widthPx by remember { mutableIntStateOf(0) }
                val widthDp = with(density) { widthPx.toDp() }
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
                        },
                        modifier = Modifier.onSizeChanged {
                            widthPx = it.width
                        }
                    )
                    ObDropdownMenuItem(
                        text = "分享",
                        leadingIcon = R.drawable.share,
                        onClick = {
                            shareText(context, "${state.entry.title} \n ${state.entry.url}", state.entry.title)
                            expandedState.targetState = false
                        },
                        modifier = Modifier.width(widthDp)
                    )
                    ObDropdownMenuItem(
                        text = "复制链接",
                        leadingIcon = R.drawable.link,
                        onClick = {
                            copyText(context, state.entry.url)
                            ObToastManager.show("Link copied")
                            expandedState.targetState = false
                        },
                        modifier = Modifier.width(widthDp)
                    )
                    ObDropdownMenuItem(
                        text = "阅读设置",
                        leadingIcon = R.drawable.brush,
                        modifier = Modifier.width(widthDp),
                        onClick = {
                            expandedState.targetState = false
                            showDisplaySettingSheet = true
                        }
                    )
                }
            }

        }
    }

}