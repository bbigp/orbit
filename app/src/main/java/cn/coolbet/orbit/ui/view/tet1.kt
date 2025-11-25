package cn.coolbet.orbit.ui.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text




import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
data class ListItem(val id: Int, val title: String)
data class MenuItem(val text: String, val onClick: () -> Unit)
// --- 假设这个是你的列表项内容 ---
@Composable
fun ContextualItemContent(item: ListItem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFFE0E0E0))
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = item.title, color = Color.Black)
    }
}

// 核心：实现 ContextMenu 效果
@Composable
fun IOSLikeContextMenu(
    // 目标内容（例如你的 ContextualItemContent）
    targetContent: @Composable (modifier: Modifier) -> Unit,
    // 菜单项
    menuItems: List<MenuItem>,
) {
    // 1. 状态：是否显示全屏菜单
    var showContextMenu by remember { mutableStateOf(false) }

    // 2. 目标元素的位置和大小 (屏幕全局坐标)
    var targetPosition by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var targetSize by remember { mutableStateOf(IntSize.Zero) }

    // Density 用于像素到 Dp 的转换
    val density = LocalDensity.current

    // 3. 目标内容
    val targetModifier = Modifier
        // 🌟 获取目标元素的全局位置和大小
        .onGloballyPositioned { coordinates ->
            targetPosition = coordinates.localToRoot(androidx.compose.ui.geometry.Offset.Zero)
            targetSize = coordinates.size
        }
        // 🌟 长按手势检测
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    showContextMenu = true
                }
            )
        }

    // 渲染目标内容
    targetContent(targetModifier)

    // 4. 浮动菜单 (使用 Dialog 实现全屏屏蔽效果)
    if (showContextMenu) {
        Dialog(
            onDismissRequest = { showContextMenu = false },
            properties = DialogProperties(usePlatformDefaultWidth = false) // 禁用默认宽度
        ) {
            val targetHeightDp = with(density) { targetSize.height.toDp() }
            val targetWidthDp = with(density) { targetSize.width.toDp() }
            val targetYOffsetDp = with(density) { targetPosition.y.toDp() }
            val targetXOffsetDp = with(density) { targetPosition.x.toDp() }

            // 全屏 Box 实现遮罩和定位
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // 🌟 背景屏蔽效果
                    .clickable { showContextMenu = false } // 点击屏蔽区关闭
            ) {
                // --- 5. 突显元素 ---
                Box(
                    modifier = Modifier
                        .offset(x = targetXOffsetDp, y = targetYOffsetDp)
                        .size(width = targetWidthDp, height = targetHeightDp)
                        // 🌟 突显效果：可选添加阴影、边框或缩放
                        .background(Color.White)
                        .padding(2.dp) // 模拟轻微边框
                ) {
                    // 重新渲染目标内容，使其在屏蔽层上突显
                    ContextualItemContent(item = ListItem(0, "被选中的项目"), modifier = Modifier.fillMaxSize())
                }

                // --- 6. 菜单内容 (位于突显元素下方) ---
                Column(
                    modifier = Modifier
                        // 定位到突显元素下方
                        .offset(x = targetXOffsetDp, y = targetYOffsetDp + targetHeightDp)
                        .width(targetWidthDp) // 菜单宽度与目标元素一致
                        .background(Color.White)
                ) {
                    menuItems.forEach { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clickable {
                                    item.onClick()
                                    showContextMenu = false
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(text = item.text, color = Color.Black)
                        }
                        // 可选：添加分割线
                        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.Gray.copy(alpha = 0.2f)))
                    }
                }
            }
        }
    }
}

@Composable
fun ContextMenuDemoScreen() {
    val items = remember {
        listOf(
            ListItem(1, "聊天记录 A"),
            ListItem(2, "重要消息 B"),
            ListItem(3, "草稿箱 C"),
            ListItem(4, "未读 D")
        )
    }

    val menuItems = listOf(
        MenuItem("回复") { println("回复") },
        MenuItem("置顶") { println("置顶") },
        MenuItem("删除") { println("删除") }
    )

    Column(Modifier.fillMaxSize()) {
        items.forEach { item ->
            IOSLikeContextMenu(
                targetContent = { modifier ->
                    // 将 ContextualItemContent 传入，并让它接受外部的 modifier
                    ContextualItemContent(item = item, modifier = modifier)
                },
                menuItems = menuItems
            )
            Spacer(Modifier.height(1.dp).fillMaxWidth().background(Color.Gray))
        }
    }
}
