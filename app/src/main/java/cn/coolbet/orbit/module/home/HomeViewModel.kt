package cn.coolbet.orbit.module.home

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.coolbet.orbit.remote.miniflux.FeedApi
import cn.coolbet.orbit.remote.miniflux.FeedResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val  feedApi: FeedApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFeeds()
    }

    fun loadFeeds() {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    feedApi.getFeeds()
                }
            }.onSuccess { feeds ->

            }.onFailure { throwable ->

            }
        }
    }

}

sealed interface HomeUiState {
    data object Initial : HomeUiState
    data object Loading : HomeUiState
    data class Success(val feeds: List<FeedResponse>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}