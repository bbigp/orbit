package cn.coolbet.orbit.module.home

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.coolbet.orbit.MemoryStore
import cn.coolbet.orbit.dao.FeedMapper
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.remote.miniflux.FeedApi
import cn.coolbet.orbit.remote.miniflux.FeedResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val memoryStore: MemoryStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d("HomeViewModel", "ViewModel initialized.")
        val feedsFlow = memoryStore.allFeeds()
        val foldersFlow = memoryStore.allFolders()

        combine(feedsFlow, foldersFlow) { feeds, folders ->
            Pair(feeds, folders)
        }.onStart {
            _uiState.update { it.copy(isLoading = true) }
        }.onEach { (feeds, folders) ->
            _uiState.update { it.copy(feeds = feeds, isLoading = false) }
        }.launchIn(viewModelScope)
    }



}

data class HomeUiState (
    val feeds: List<Feed> = emptyList(),
    val isLoading: Boolean = false,
    val hasMore: Boolean = false,
)