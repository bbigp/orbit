package cn.coolbet.orbit.ui.view.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.domain.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileScreenModel(
    val cacheStore: CacheStore,
    private val session: Session,
): ScreenModel {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()


    init {
        combine(session.state, Env.settings.rootFolder.state) { user, rootFolderId ->
            Pair(user, rootFolderId)
        }.onStart {
            _state.update { it.copy(isLoading = true) }
        }.onEach { (user, rootFolderId) ->
            _state.update {
                it.copy(
                    isLoading = false, user = user,
                    rootFolder = cacheStore.folder(rootFolderId)
                )
            }
        }.launchIn(screenModelScope)
    }

    fun logout() {
        session.endSession()
        NavigatorBus.replaceAll(Route.Login)
    }

    fun deleteLocalData() {
        screenModelScope.launch {
            cacheStore.deleteLocalData()
        }
    }
}

data class ProfileState (
    val user: User = User.EMPTY,
    val rootFolder: Folder = Folder.EMPTY,
    val isLoading: Boolean = false,
)