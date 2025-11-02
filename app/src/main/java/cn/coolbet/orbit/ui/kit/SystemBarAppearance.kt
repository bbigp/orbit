package cn.coolbet.orbit.ui.kit

import android.app.Activity
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SystemBarAppearance(
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val view = LocalView.current

    // 使用 SideEffect 来确保主题变化时，系统栏外观也随之变化
    SideEffect {
        // 1. 获取当前 Window
        val window = (view.context as Activity).window

        // 2. 设置状态栏图标颜色
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme  //顶部状态栏 电量 时间 通知
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                isAppearanceLightNavigationBars = !darkTheme //底部导航栏  主页 返回
            }
        }
    }
}

@Composable
fun SystemBarStyleModern(
    statusBarColor: Color,
    isLightStatusBars: Boolean // false = 深色图标 (浅色背景), true = 浅色图标 (深色背景)
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity ?: return

    // 使用 DisposableEffect 来设置和清理系统栏样式
    DisposableEffect(statusBarColor, isLightStatusBars) {

        // 1. 根据期望的图标颜色选择 SystemBarStyle
        val style = if (isLightStatusBars) {
            // 需要浅色图标 → 使用 dark 风格，背景设置为你的颜色
            SystemBarStyle.dark(statusBarColor.toArgb())
        } else {
            // 需要深色图标 → 使用 light 风格，背景设置为你的颜色
            SystemBarStyle.light(
                statusBarColor.toArgb(),
                statusBarColor.toArgb() // 导航栏颜色可以一样
            )
        }

        // 2. 应用样式
        activity.enableEdgeToEdge(statusBarStyle = style)

        onDispose {
            // 可选：在 Composable 退出时，恢复默认的系统栏样式
            activity.enableEdgeToEdge() // 恢复默认通常是透明
        }
    }
}