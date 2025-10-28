package cn.coolbet.orbit.view.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.coolbet.orbit.MemoryStore
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    memoryStore: MemoryStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d("HomeViewModel", "ViewModel initialized.")
        val feedsFlow = memoryStore.allFeeds()
        val foldersFlow = memoryStore.allFolders()

        combine(feedsFlow, foldersFlow) { feeds, folders ->
            Pair(feeds, folders)
        }
        .onStart {
            _uiState.update { it.copy(isLoading = true) }
        }
        .onEach { (feeds, folders) ->
            _uiState.update { it.copy(isLoading = false, feeds = feeds, folders = folders) }
        }
       .launchIn(viewModelScope)
    }

    fun toggleExpanded(folderId: Long) {
        _uiState.update { currentState ->
            val updatedFolders = currentState.folders.map { folder ->
                if (folder.id == folderId) folder.copy(expanded = !folder.expanded) else folder
            }
            currentState.copy(folders = updatedFolders)
        }
    }

}

data class HomeUiState (
    val feeds: List<Feed> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val isLoading: Boolean = false,
    val hasMore: Boolean = false,
)