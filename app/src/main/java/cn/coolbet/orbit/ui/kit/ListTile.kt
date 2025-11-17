package cn.coolbet.orbit.ui.kit

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black25
import cn.coolbet.orbit.ui.theme.Black50
import cn.coolbet.orbit.ui.theme.ObTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//headlineContent  title  主标题
//overlineContent 上标题
//supportingContent  subtitle 副标题 下标题
//leadingContent leading  左侧
//trailingContent trailing 右侧

// 定义菜单的最大宽度，防止菜单过宽超出屏幕
const val MAX_MENU_WIDTH_DP = 240

// 2. 测量菜单内容的 Composable 块
val menuContent: @Composable () -> Unit = {
    // 使用实际的菜单内容来测量宽度
    DropdownMenuItem(text = { Text("选项一") }, onClick = {})
    DropdownMenuItem(text = { Text("选项二很长很长") }, onClick = {})
}

@Composable
fun MeasureView() {
    var menuContentWidth by remember { mutableIntStateOf(0) } // 1. 存储测量到的菜单内容宽度 (像素值)
    // --- 测量菜单宽度的 SubcomposeLayout ---
    SubcomposeLayout(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd) // 保持右侧对齐
    ) { constraints ->
        // a) 测量菜单内容
        val subcomposeMeasurable = subcompose(Unit, menuContent)
        val placeable = subcomposeMeasurable.firstOrNull()?.measure(
            constraints.copy(
                maxWidth = constraints.maxWidth.coerceAtMost(MAX_MENU_WIDTH_DP.dp.roundToPx())
            )
        )

        // b) 更新菜单内容宽度
        menuContentWidth = placeable?.width ?: 0

        // 由于 SubcomposeLayout 在这里只用于测量，我们返回一个空的布局
        layout(0, 0) {}
    }
}

@Composable
fun ListTileChevronUpDown(
    title: String, trailing: String, icon: Int,
    menuContent: @Composable () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var anchorSize by remember { mutableStateOf(IntSize.Zero) }
    var menuContentWidth by remember { mutableIntStateOf(240) }
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(ObTheme.colors.primaryContainer)
            .clickable{
                scope.launch {
                    if (!expanded) {
                        expanded = true
                    } else {
                        expanded = false
                        delay(50)
                    }
                    val targetState = !expanded
                    expanded = targetState

                    if (!targetState) {
                        // 如果我们正在关闭，等待一个极小的时间，让事件穿透和处理完成。
                        // 这样可以避免在 onDismissRequest 将其关闭后，Row 的 clickable
                        // 紧接着将其打开。
                        delay(10) // 只需要一个极小的延迟
                    }
                }
            },
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
                .onSizeChanged { anchorSize = it } // 记录锚点 Box 的实际尺寸
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

            CustomDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                Column {
                    menuContent()
                }
            }
        }
    }

//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(48.dp)
//            .background(ObTheme.colors.primaryContainer)
//            .clickable(
//                indication = null,
//                interactionSource = interactionSource,
//                onClick = { expanded = !expanded }
//            ),
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Spacer(modifier = Modifier.width(16.dp))
//        Image(
//            modifier = Modifier.size(24.dp),
//            painter = painterResource(id = icon),
//            contentDescription = "",
//            contentScale = ContentScale.Fit,
//            colorFilter = ColorFilter.tint(ObTheme.colors.secondary),
//        )
//        Spacer(modifier = Modifier.width(12.dp))
//        Text(
//            title,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            style = AppTypography.R15,
//            modifier = Modifier.weight(1f)
//        )
//        Spacer(modifier = Modifier.width(16.dp)) // 12 + 4
//
//        // --- 关键区域：局部 Box 锚点 ---
//        Box(
//            // 关键：在这里测量 Box 的尺寸
//            modifier = Modifier
//                .fillMaxHeight()
//                .wrapContentSize(Alignment.TopEnd)
//                .onSizeChanged { anchorSize = it } // 记录锚点 Box 的实际尺寸
//        ) {
//            // 1. 锚点内容（右侧的 Text 和 Image）
//            Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
//                Text(trailing, maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R15B50)
//                Spacer(modifier = Modifier.width(4.dp)) //4 + 4
//                Image(
//                    modifier = Modifier.size(20.dp),
//                    painter = painterResource(id = R.drawable.chevron_up_down),
//                    contentDescription = "",
//                    contentScale = ContentScale.Inside,
//                    colorFilter = ColorFilter.tint(ObTheme.colors.tertiary),
//                )
//            }
//
//            // 2. DropdownMenu 锚定到此 Box
//            DropdownMenu(
//                containerColor = ObTheme.colors.primaryContainer,
//                tonalElevation = 0.dp,//阴影
//                shape = RoundedCornerShape(16.dp),
//                expanded = expanded,
//                onDismissRequest = { expanded = false },
////                modifier = Modifier.padding(vertical = (-4).dp)
////                    .offset { // 响应式偏移计算
////                    // X_offset = 锚点 Box 宽度 - 菜单实际宽度 (测量值)
////                    // 这个计算确保菜单的右边缘对齐到锚点 Box 的右边缘
////                    val horizontalOffset = anchorSize.width - menuContentWidth
////                    IntOffset(x = horizontalOffset, y = 0)
////                }
//            ) {
//                menuContent()
//            }
//        }
//        Spacer(modifier = Modifier.width(12.dp))
//    }
}


@Composable
fun ListTileChevronRight(title: String, icon: Int) {
    Row (
        modifier = Modifier.fillMaxWidth().height(48.dp)
            .background(ObTheme.colors.primaryContainer)
            .padding(start = 16.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(ObTheme.colors.secondary),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R15, modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(16.dp)) // 12 + 4
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.chevron_right),
            contentDescription = "",
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(ObTheme.colors.tertiary),
        )
    }
}

@Composable
fun ListTileSwitch(
    title: String, icon: Int,
    checked: Boolean = false, onCheckedChange: (Boolean) -> Unit = {},
) {
    Row (
        modifier = Modifier.fillMaxWidth().height(48.dp)
            .background(ObTheme.colors.primaryContainer)
            .padding(start = 16.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(ObTheme.colors.secondary),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R15, modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(12.dp))
        Spacer(modifier = Modifier.width(44.dp)) //40 + 4
        ObSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Spacer(modifier = Modifier.width(4.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewListTileChevronUpDown() {
    Column {
        ListTileChevronUpDown(title = "Unread Mark", trailing = "None", icon = R.drawable.book)
        SpacerDivider(start = 52.dp, end = 16.dp)
        ListTileChevronRight(title = "Swipe Gestures", icon = R.drawable.brush)
        SpacerDivider(start = 52.dp, end = 16.dp)
        ListTileSwitch(title = "Automatic Reader View", icon = R.drawable.brush)
        SpacerDivider(start = 52.dp, end = 16.dp)
    }
}