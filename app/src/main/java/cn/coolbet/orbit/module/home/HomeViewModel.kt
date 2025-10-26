package cn.coolbet.orbit.module.home

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.coolbet.orbit.dao.FeedMapper
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.remote.miniflux.FeedApi
import cn.coolbet.orbit.remote.miniflux.FeedResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedMapper: FeedMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(isLoading = false, feeds = feedMapper.getFeeds()) }
        }
    }

}

data class HomeUiState (
    val feeds: List<Feed> = emptyList(),
    val isLoading: Boolean = false,
    val hasMore: Boolean = false,
)