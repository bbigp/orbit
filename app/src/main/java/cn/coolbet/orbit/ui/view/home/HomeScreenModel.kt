package cn.coolbet.orbit.ui.view.home

import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class HomeScreenModel(
    cacheStore: CacheStore,
    session: Session,
) : ScreenModel {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    val unreadMapState: StateFlow<Map<String, Int>> = cacheStore.unreadMapState
    val userState: StateFlow<User> = session.state

    init {
        Log.d("HomeViewModel", "ViewModel initialized.")
        combine(cacheStore.feedsState, cacheStore.foldersState, Env.settings.rootFolder.state) { feeds, folders, rootFolderId ->
            CacheData(feeds, folders, rootFolderId)  //Pair Triple
        }
        .onStart {
            _uiState.update { it.copy(isLoading = true) }
        }
        .onEach { (feeds, folders, rootFolderId) ->
            _uiState.update { it ->
                it.copy(isLoading = false,
                    feeds = feeds.asSequence().filter { it.folderId == rootFolderId }.toList(),
                    folders = folders.asSequence().filter { it.id != rootFolderId }.toList()
                )
            }
        }
       .launchIn(screenModelScope)
    }

    fun toggleExpanded(folderId: Long) {
        _uiState.update { currentState ->
            val updatedFolders = currentState.folders.map { folder ->
                if (folder.id == folderId) folder.copy(expanded = !folder.expanded) else folder
            }
            currentState.copy(folders = updatedFolders)
        }
    }

    override fun onDispose() {
        Log.d("HomeScreenModel", "ScreenModel disposed.")
        // super.onDispose()
    }

}

data class CacheData(
    val feeds: List<Feed>,
    val folders: List<Folder>,
    val rootFolderId: Long
)

data class HomeScreenState (
    val feeds: List<Feed> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val isLoading: Boolean = false,
    val hasMore: Boolean = false,
)