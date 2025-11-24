package cn.coolbet.orbit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.ui.view.entries.EntriesScreen
import cn.coolbet.orbit.ui.view.home.HomeScreen
import cn.coolbet.orbit.ui.view.login.LoginScreen
import cn.coolbet.orbit.ui.view.profile.ProfileScreen
import cn.coolbet.orbit.ui.view.search_entries.SearchEntriesScreen
import cn.coolbet.orbit.ui.view.sync.SyncScreen
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Suppress("FunctionName")
data class Route(val screen: Screen) {
    companion object {
        val Login = Route(screen = LoginScreen)
        val Home = Route(screen = HomeScreen)
        val Profile = Route(screen = ProfileScreen)
        val Sync = Route(screen = SyncScreen)
        fun Entries(metaId: MetaId) = Route(
            screen = EntriesScreen(metaId)
        )
        fun SearchEntries(meta: Meta) = Route(
            screen = SearchEntriesScreen(meta)
        )
    }
}

sealed class NavigationEvent {
    data class Push(val route: Route): NavigationEvent()
    data class ReplaceAll(val route: Route): NavigationEvent()
    data class Replace(val route: Route): NavigationEvent()
    object Pop: NavigationEvent()
    data class PopUntil(val targetRoute: Route): NavigationEvent()
}

@Composable
fun OrbitRouter() {
    val navigator = LocalNavigator.currentOrThrow
    LaunchedEffect(Unit) {
        NavigatorBus.events.collect { it ->
            when(it){
                is NavigationEvent.Pop -> navigator.pop()
                is NavigationEvent.Push -> navigator.push(it.route.screen)
                is NavigationEvent.Replace -> navigator.replace(it.route.screen)
                is NavigationEvent.PopUntil -> {
                    navigator.popUntil { screenInStack ->
                        screenInStack::class == it.targetRoute.screen::class
                    }
                }
                is NavigationEvent.ReplaceAll -> navigator.replaceAll(it.route.screen)
            }
        }
    }

}

// 放在 Application 级别的单例对象中
object NavigatorBus {
    // 使用 MutableSharedFlow 作为事件总线
    // replay = 0: 不重发历史事件
    // extraBufferCapacity = 64: 允许一些缓冲，防止丢失
    private val _events = MutableSharedFlow<NavigationEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST // 允许在极端情况下丢弃最老的事件
    )

    // 供 Router 监听的只读 Flow
    val events: Flow<NavigationEvent> = _events.asSharedFlow()

    fun push(route: Route): Boolean = _events.tryEmit(NavigationEvent.Push(route))

    fun replace(route: Route): Boolean = _events.tryEmit(NavigationEvent.Replace(route))

    fun pop(): Boolean = _events.tryEmit(NavigationEvent.Pop)

    fun popUntil(targetRoute: Route): Boolean = _events.tryEmit(NavigationEvent.PopUntil(targetRoute))

    fun replaceAll(route: Route): Boolean = _events.tryEmit(NavigationEvent.ReplaceAll(route))

    // 供 View 层非协程环境使用的 trySend 函数
    fun trySend(event: NavigationEvent): Boolean {
        return _events.tryEmit(event)
    }

    // 供所有 Composable/Model 使用的发送函数
    // 这是一个 suspend 函数，保证发送成功
    suspend fun send(event: NavigationEvent) {
        _events.emit(event)
    }
}
