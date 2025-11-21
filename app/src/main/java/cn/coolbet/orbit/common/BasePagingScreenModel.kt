package cn.coolbet.orbit.common

import android.util.Log
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BasePagingScreenModel<T, E>(
    initialState: PageState<T, E>
): StateScreenModel<PageState<T, E>>(initialState) {

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
                mutableState.update { it.addItems(newData) }
            } catch (e: Exception) {
                mutableState.update { it.copy(isLoadingMore = false) }
                Log.e("BasePagingScreenModel", "加载数据出错.", e)
            }
        }
    }
}

data class PageState<T, E>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val size: Int = 20,
    val hasMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val extra: E,
)

fun <T, E> PageState<T, E>.addItems(data: List<T>, reset: Boolean = false): PageState<T, E> {
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
            isLoadingMore = false
        )
    }
}