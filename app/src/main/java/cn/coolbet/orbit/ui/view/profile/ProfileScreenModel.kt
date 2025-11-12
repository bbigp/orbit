package cn.coolbet.orbit.ui.view.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.domain.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileScreenModel @Inject constructor(
    private val store: CacheStore,
    private val session: Session,
): ScreenModel {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        _state.update { it.copy(isLoading = true) }
        val rootFolderId = session.user.rootFolder
        store.flowFolder(rootFolderId).onEach { folder ->
            _state.update { it.copy(isLoading = false, user = session.user, rootFolder = folder) }
        }.launchIn(screenModelScope)
    }

    fun logout() {
        session.endSession()
        NavigatorBus.replaceAll(Route.Login)
    }

    fun deleteLocalData() {
        screenModelScope.launch {
            store.deleteLocalData()
        }
    }
}

data class ProfileState (
    val user: User = User.EMPTY,
    val rootFolder: Folder = Folder.EMPTY,
    val isLoading: Boolean = false,
)