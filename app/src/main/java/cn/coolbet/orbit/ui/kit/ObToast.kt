package cn.coolbet.orbit.ui.kit

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import cafe.adriel.voyager.navigator.LocalNavigator
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Blue
import cn.coolbet.orbit.ui.theme.Blue10
import cn.coolbet.orbit.ui.theme.ContainerRed
import cn.coolbet.orbit.ui.theme.ContentRed
import cn.coolbet.orbit.ui.theme.Green
import cn.coolbet.orbit.ui.theme.Green10
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


enum class ToastType(val iconColor: Color, val iconColorOuter: Color) {
    Error(ContentRed, ContainerRed),
//    Warning(),
    Success(Green, Green10),
    Info(Blue, Blue10);
}

class ToastState {
    var type by mutableStateOf(ToastType.Info)
    var isVisible by mutableStateOf(false)
    var message by mutableStateOf("")
    private var job: Job? = null // 用于取消之前的计时任务

    fun show(scope: CoroutineScope, text: String, toastType: ToastType = ToastType.Info, hideInSeconds: Long = 1000) {
        job?.cancel()
        job = scope.launch {
            if (isVisible) {
                isVisible = false
                delay(50) // 短暂延迟让动画重置
            }
            type = toastType
            message = text
            isVisible = true

            // 4. 开始新一轮计时
            delay(hideInSeconds)
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
fun rememberToastState() = remember { ToastState() }

object ObToastManager {

    private val _events = MutableSharedFlow<ObToastEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun show(msg: String, type: ToastType = ToastType.Info) {
        _events.tryEmit(ObToastEvent.Show(msg, type))
    }
}

sealed class ObToastEvent {
    data class Show(val message: String, val type: ToastType) : ObToastEvent()
}


@Composable
fun ObToast() {
    val navigator = LocalNavigator.current
    val state = rememberToastState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        ObToastManager.events.collect { event ->
            when (event) {
                is ObToastEvent.Show -> state.show(scope, event.message, event.type)
            }
        }
    }

    navigator?.let { nav ->
        LaunchedEffect(nav.lastItem) {
            state.dismiss()
        }
    }

    if (state.message.isNotEmpty()) {
        Popup(
            alignment = Alignment.TopCenter,
            properties = PopupProperties(
                focusable = false,
                excludeFromSystemGesture = true // 允许 Popup 延伸到状态栏区域
            )
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
                        // 1. 使用 statusBarsPadding 确保它从状态栏最顶部开始
                        // 2. 如果你想完全盖住状态栏文字，可以微调 padding 甚至不加
                        .statusBarsPadding()
                        .padding(top = 12.dp) // 给顶端留一点极小的呼吸间距，类似灵动岛
                        .widthIn(min = 120.dp, max = 300.dp)
                        .then(if (Build.VERSION.SDK_INT >= 31) Modifier.blur(25.dp) else Modifier)
                        .shadow(15.dp, RoundedCornerShape(25.dp)), // 较重的阴影
//                    color = Color.White.copy(alpha = 0.6f), // 模拟半透明
                    shape = RoundedCornerShape(99.dp),
                    border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f)), // 增加玻璃质感
                    shadowElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            .background(Color.White.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier.size(20.dp).clip(CircleShape)
                                        .background(state.type.iconColorOuter)
                                )
                                Box(
                                    modifier = Modifier.size(8.dp).clip(CircleShape)
                                        .background(state.type.iconColor)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                state.message,
                                modifier = Modifier.padding(horizontal = 4.dp),
                                style = AppTypography.M15,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}