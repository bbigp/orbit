package cn.coolbet.orbit.ui.kit

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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