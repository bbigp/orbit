package cn.coolbet.orbit.ui.kit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.ToastBus
import cn.coolbet.orbit.ToastEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class IosToastState {
    var message by mutableStateOf("")
    var isVisible by mutableStateOf(false)
    private var job: Job? = null // 用于取消之前的计时任务

    fun show(msg: String, scope: CoroutineScope) {
        job?.cancel()
        job = scope.launch {
            if (isVisible) {
                isVisible = false
                delay(50) // 短暂延迟让动画重置
            }
            // 给一小段延迟（如50ms），让 Compose 触发重组完成“退出-进入”的动画切换
//            delay(50)
            message = msg
            isVisible = true

            // 4. 开始新一轮计时
            delay(2000)
            isVisible = false

            // 5. 动画完全结束后清空文字
            delay(300)
            message = ""
        }
    }

    fun dismiss() {
        job?.cancel()
        isVisible = false
        message = ""
    }
}

@Composable
fun rememberIosToastState() = remember { IosToastState() }

@Composable
fun ObToast() {
    val navigator = LocalNavigator.currentOrThrow

    val toastState = rememberIosToastState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        ToastBus.events.collect { event ->
            when (event) {
                is ToastEvent.Show -> toastState.show(event.message, scope)
            }
        }
    }

    LaunchedEffect(navigator.lastItem) {
        toastState.dismiss()
    }




    if (toastState.message.isNotEmpty()) {
        Popup(
            alignment = Alignment.TopCenter,
            properties = PopupProperties(
                focusable = false,
                // 允许 Popup 延伸到状态栏区域
                excludeFromSystemGesture = true
            )
        ) {
            AnimatedVisibility(
                visible = toastState.isVisible,
                // iOS 风格通常配合缩放(Scale)和淡入
                enter = fadeIn(animationSpec = tween(300)) +
                        scaleIn(initialScale = 0.8f, animationSpec = tween(300)) +
                        slideInVertically(initialOffsetY = { -it / 2 }),
                exit = fadeOut(animationSpec = tween(300)) +
                        scaleOut(targetScale = 0.8f, animationSpec = tween(300)) +
                        slideOutVertically(targetOffsetY = { -it / 2 })
            ) {
                Surface(
                    modifier = Modifier
                        // 1. 使用 statusBarsPadding 确保它从状态栏最顶部开始
                        // 2. 如果你想完全盖住状态栏文字，可以微调 padding 甚至不加
                        .statusBarsPadding()
                        .padding(top = 12.dp) // 给顶端留一点极小的呼吸间距，类似灵动岛
                        .widthIn(min = 120.dp, max = 300.dp)
                        .shadow(15.dp, RoundedCornerShape(25.dp)), // 较重的阴影
                    color = Color.White.copy(alpha = 0.8f), // 模拟半透明
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f)) // 极细的边框增加质感
                ) {
                    // 如果你的 Android 版本支持（API 31+），可以给这个 Box 加 .blur(10.dp)
                    Box(
                        modifier = Modifier.height(40.dp).padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = toastState.message,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

//3. 如何获得真正的“毛玻璃”效果？
//Android 原生对高斯模糊的处理比较吃性能。如果你的 App 只跑在 Android 12 (API 31) 及以上，可以直接用 Modifier.blur()。如果是通用版本，iOS 风格通常用 半透明纯色 配合 细微的内发光/边框 来模拟。
//
//// Android 12+ 开启毛玻璃
//Surface(
//modifier = Modifier
//.blur(if (Build.VERSION.SDK_INT >= 31) 20.dp else 0.dp)
//// ... 其他参数
//)

//enum ToastType {
//    case error
//    case warning
//    case success
//    case info
//}
//
//extension ToastType {
//    var iconColor: Color {
//        switch self {
//            case .error:
//            return .red100
//            case .warning:
//            return .orange100
//            case .success:
//            return .green100
//            case .info:
//            return .blue100
//        }
//    }
//
//    var iconColorOuter: Color {
//        switch self {
//            case .error:
//            return .red10
//            case .warning:
//            return .orange10
//            case .success:
//            return .green10
//            case .info:
//            return .blue10
//        }
//    }
//}
