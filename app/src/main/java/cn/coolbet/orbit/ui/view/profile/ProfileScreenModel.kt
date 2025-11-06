package cn.coolbet.orbit.ui.view.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.MemoryStore
import cn.coolbet.orbit.manager.SessionManager
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.domain.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileScreenModel @Inject constructor(
    private val store: MemoryStore,
    private val sessionManager: SessionManager,
): ScreenModel {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        store.currentUser()
            .flatMapLatest { user ->
                val rootFolderId = user.rootFolder
                store.folder(rootFolderId).map { folder -> Pair(user, folder) }
            }
            .onStart {
                _state.update { it.copy(isLoading = true) }
            }
            .onEach { (user, folder) ->
                _state.update { it.copy(isLoading = false, user = user, rootFolder = folder) }
            }
            .launchIn(screenModelScope)
    }

    fun logout() {
        sessionManager.endSession()
    }
}

data class ProfileState (
    val user: User = User.EMPTY,
    val rootFolder: Folder = Folder.EMPTY,
    val isLoading: Boolean = false,
)