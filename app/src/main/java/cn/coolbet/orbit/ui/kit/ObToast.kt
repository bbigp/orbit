package cn.coolbet.orbit.ui.kit


class IosToastState {
    var message by mutableStateOf("")
    var isVisible by mutableStateOf(false)
    private var job: Job? = null // 用于取消之前的计时任务

    fun show(msg: String, scope: CoroutineScope) {
        message = msg
        isVisible = true

        // 如果之前有一个正在计时的任务，直接取消它，重新开始计时
        job?.cancel()
        job = scope.launch {
            delay(2500) // iOS Toast 通常停留稍久一点
            isVisible = false
            delay(300) // 等待退出动画
            message = ""
        }
    }
}

@Composable
fun IosTopToast(state: IosToastState) {
    if (state.message.isNotEmpty()) {
        Popup(
            alignment = Alignment.TopCenter,
            properties = PopupProperties(focusable = false)
        ) {
            AnimatedVisibility(
                visible = state.isVisible,
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
                        .padding(top = 50.dp)
                        .widthIn(min = 120.dp, max = 300.dp)
                        .shadow(15.dp, RoundedCornerShape(25.dp)), // 较重的阴影
                    color = Color.White.copy(alpha = 0.8f), // 模拟半透明
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f)) // 极细的边框增加质感
                ) {
                    // 如果你的 Android 版本支持（API 31+），可以给这个 Box 加 .blur(10.dp)
                    Box(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            style = TextStyle(
                                color = Color.Black.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            ),
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
