package cn.coolbet.orbit.ui.kit

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.kit.DragHandle

/**
 * Helper to show a composable as a Voyager bottom sheet by wrapping it into an anonymous Screen.
 * - `showDragHandle`: when true (default) renders the project's `DragHandle` above the content.
 * - `dragContent`: optional custom composable to render instead of the default `DragHandle`.
 * Usage: `sheetNavigator.showSheet(showDragHandle = true) { YourComposable(...) }`
 */
fun BottomSheetNavigator?.showSheet(
    showDragHandle: Boolean = true,
    dragContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    this?.show(object : Screen {
        @Composable
        override fun Content() {
            Column {
                if (showDragHandle) {
                    if (dragContent != null) dragContent() else DragHandle()
                }
                content()
            }
        }
    })
}


fun BottomSheetNavigator?.showFlow(
    showDragHandle: Boolean = true,
    dragContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    this?.show(object : Screen {
        @Composable
        override fun Content() {
            Column {
                if (showDragHandle) {
                    if (dragContent != null) dragContent() else DragHandle()
                }
                content()
            }
        }
    })
}