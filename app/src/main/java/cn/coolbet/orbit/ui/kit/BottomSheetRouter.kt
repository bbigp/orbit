package cn.coolbet.orbit.ui.kit

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class NavDir { Forward, Backward }
typealias Push = (@Composable () -> Unit) -> Unit
typealias Pop = () -> Unit

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun BottomSheetRouter(
    initialPage: @Composable (push: Push, pop: Pop) -> Unit
) {
    // 栈里直接存的是 @Composable 函数（即页面本身）
    val stack = remember { mutableStateListOf<@Composable () -> Unit>() }
    var direction by remember { mutableStateOf(NavDir.Forward) }

    // 统一定义 Push 和 Pop 逻辑，方便内部递归调用
    val push: Push = { newPage ->
        direction = NavDir.Forward
        stack.add(newPage)
    }
    val pop: Pop = {
        if (stack.size > 1) {
            direction = NavDir.Backward
            stack.removeAt(stack.size - 1)
        }
    }

    // 初始化加载
    if (stack.isEmpty()) {
        stack.add { initialPage(push, pop) }
    }

    BackHandler(enabled = stack.size > 1) { pop() }

    AnimatedContent(
        targetState = stack.last(), // 动态获取栈顶函数执行
        transitionSpec = {
            if (direction == NavDir.Forward) {
                slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
            } else {
                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
            }
        }
    ) { currentContent ->
        currentContent() // 直接运行当前的页面内容
    }
}
