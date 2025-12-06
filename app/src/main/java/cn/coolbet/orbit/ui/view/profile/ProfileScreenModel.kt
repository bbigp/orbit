package cn.coolbet.orbit.ui.view.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.manager.Preference
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.domain.OpenContentWith
import cn.coolbet.orbit.model.domain.UnreadMark
import cn.coolbet.orbit.model.domain.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileScreenModel @Inject constructor(
    private val store: CacheStore,
    private val session: Session,
    private val preference: Preference,
): ScreenModel {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val folders = store.foldersState

    init {
        _state.update { it.copy(isLoading = true) }
        session.state
            .filter { it.isNotEmpty }
            .flatMapLatest { user ->
                store.flowFolder(user.rootFolder)
                    .map { folder ->
                        folder to user
                    }
            }
            .onEach { (folder, user) ->
                _state.update { it.copy(isLoading = false, user = user, rootFolder = folder) }
            }
            .launchIn(screenModelScope)
    }

    fun logout() {
        session.endSession()
        NavigatorBus.replaceAll(Route.Login)
    }

    fun changeUser(unreadMark: UnreadMark? = null, autoRead: Boolean? = null,
                   openContent: OpenContentWith? = null, rootFolderId: Long? = null,
                   autoReaderView: Boolean? = null,
    ) {
        val newUser = preference.userSetting(unreadMark = unreadMark, autoRead = autoRead,
            openContent = openContent, rootFolderId = rootFolderId,
            autoReaderView = autoReaderView,
        )
        session.startSession(newUser)
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