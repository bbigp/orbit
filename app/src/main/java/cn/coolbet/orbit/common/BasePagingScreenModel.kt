package cn.coolbet.orbit.common

import android.util.Log
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class IPagingScreenModel<T>(
    initialState: PageState<T>
): StateScreenModel<PageState<T>>(initialState) {

    abstract suspend fun fetchData(page: Int, size: Int): List<T>

    open fun loadInitialData() {
        screenModelScope.launch {
            if (state.value.isRefreshing) return@launch
            mutableState.update { it.copy(isRefreshing = true) }
            try {
                val newData = fetchData(page = 1, size = state.value.size)
                mutableState.update { it.addItems(newData, reset = true) }
            } catch (e: Exception) {
                mutableState.update { it.copy(isRefreshing = false) }
                Log.e("BasePagingScreenModel", "加载数据出错", e)
            }
        }
    }

    fun nextPage() {
        screenModelScope.launch {
            if (!state.value.hasMore) return@launch
            if (state.value.isLoadingMore) return@launch
            mutableState.update { it.copy(isLoadingMore = true) }
            try {
                val newData = fetchData(page = state.value.page + 1, size = state.value.size)
                delay(200)
                mutableState.update { it.addItems(newData) }
            } catch (e: Exception) {
                mutableState.update { it.copy(isLoadingMore = false) }
                Log.e("BasePagingScreenModel", "加载数据出错.", e)
            }
        }
    }
}

interface ILoadingState {
    val hasMore: Boolean
    val isRefreshing: Boolean
    val isLoadingMore: Boolean
}

data class PageState<T>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val size: Int = 20,
    override val hasMore: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
): ILoadingState

fun <T> PageState<T>.addItems(data: List<T>, reset: Boolean = false): PageState<T> {
    val newHasMore = data.size >= this.size
    return if (reset) {
        this.copy(
            items = data,
            page = 1,
            hasMore = newHasMore,
            isRefreshing = false,
        )
    } else {
        this.copy(
            items = this.items + data,
            page = this.page + 1,
            hasMore = newHasMore,
            isLoadingMore = false,
        )
    }
}