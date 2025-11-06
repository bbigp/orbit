package cn.coolbet.orbit

sealed class AppRoute {
    data object Home : AppRoute()
    data object Login : AppRoute()
    data object Settings : AppRoute()
    // ... 不再是 Screen 实例，只是一个名字/标识符
}

// 对应修改 BaseUiEvent:
sealed class BaseUiEvent {
    // ⭐️ 关键：不再传递具体的 Screen 实例，而是传递抽象的 AppRoute
    data class NavigateTo(val route: AppRoute, val replaceAll: Boolean = false): BaseUiEvent()
    // ...
}

class LoginScreenModel @Inject constructor(...) : ScreenModel {
    // ...
    fun login() {
        // ... 登录成功逻辑 ...

        // 🚀 发送抽象的目的地标识符
        _events.send(BaseUiEvent.NavigateTo(route = AppRoute.Home, replaceAll = true))
    }
}

object AppScreenMapper {
    fun map(route: AppRoute): Screen {
        return when (route) {
            AppRoute.Home -> MainScreen // 具体的 Screen 实例
            AppRoute.Login -> LoginScreen()
            AppRoute.Settings -> SettingsScreen()
        }
    }
}

LaunchedEffect(Unit) {
    model.events.collect { event ->
        when (event) {
            is BaseUiEvent.NavigateTo -> {
                // ⭐️ 关键：将抽象 Route 转换为具体的 Screen
                val targetScreen = AppScreenMapper.map(event.route)

                if (event.replaceAll) {
                    navigator.replaceAll(targetScreen)
                } else {
                    navigator.push(targetScreen)
                }
            }
            // ...
        }
    }
}