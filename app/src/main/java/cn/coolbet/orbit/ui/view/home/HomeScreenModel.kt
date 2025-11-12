package cn.coolbet.orbit.ui.view.home

import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class HomeScreenModel @Inject constructor(
    cacheStore: CacheStore
) : ScreenModel {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    init {
        Log.d("HomeViewModel", "ViewModel initialized.")
        val feedsFlow = cacheStore.flowAllFeeds()
        val foldersFlow = cacheStore.flowAllFolders()

        combine(feedsFlow, foldersFlow) { feeds, folders ->
            Pair(feeds, folders)
        }
        .onStart {
            _uiState.update { it.copy(isLoading = true) }
        }
        .onEach { (feeds, folders) ->
            val rootFolder  = 1L;
            _uiState.update {
                it.copy(isLoading = false,
                    feeds = feeds.asSequence().filter { it.folderId == rootFolder }.toList(),
                    folders = folders.asSequence().filter { it.id != rootFolder }.toList()
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

data class HomeScreenState (
    val feeds: List<Feed> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val isLoading: Boolean = false,
    val hasMore: Boolean = false,
)