package cn.coolbet.orbit.ui.view.entries

import android.util.Log
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.common.ILoadingState
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class EntriesScreenModel @Inject constructor(
    private val entryManager: EntryManager,
    private val cacheStore: CacheStore,
): StateScreenModel<EntriesState>(initialState = EntriesState()) {

    val unreadMapState: StateFlow<Map<String, Int>> = cacheStore.unreadMapState

    init {
        mutableState.update { it.copy(isRefreshing = true) }
    }

    fun refresh(){
        val id = state.value.meta.metaId
        this.clearState()
        loadInitialData(id)
    }

    fun loadInitialData(metaId: MetaId) {
        val value = state.value
        if (value.isRefreshing && value.meta.isNotEmpty) return
        val metaDataFlow: Flow<Meta> = when {
            metaId.isFeed -> cacheStore.flowFeed(metaId.id)
            metaId.isFolder -> cacheStore.flowFolder(metaId.id)
            else -> {
                throw IllegalStateException("MetaId type is neither Feed nor Folder.")
            }
        }
        screenModelScope.launch {
            delay(200)
            try {
                val meta = metaDataFlow.first()
                val newData = entryManager.getPage(meta, page = 1, size = value.size)
                mutableState.update { it.addItems(newData, reset = true, meta) }
            } catch (e: Exception) {
                mutableState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun nextPage() {
        screenModelScope.launch {
            if (!state.value.hasMore) return@launch
            if (state.value.isLoadingMore) return@launch
            mutableState.update { it.copy(isLoadingMore = true) }
            try {
                val newData = entryManager.getPage(
                    state.value.meta,
                    page = state.value.page + 1,
                    size = state.value.size
                )
                delay(200)
                mutableState.update { it.addItems(newData) }
            } catch (e: Exception) {
                mutableState.update { it.copy(isLoadingMore = false) }
                Log.e("BasePagingScreenModel", "加载数据出错.", e)
            }
        }
    }

    fun clearState() {
        mutableState.update { EntriesState(isRefreshing = true) }
    }

}

data class EntriesState(
    val meta: Meta = Feed.EMPTY,
    val items: List<Entry> = emptyList(),
    val page: Int = 1,
    val size: Int = 20,
    override val hasMore: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
): ILoadingState

fun EntriesState.addItems(data: List<Entry>, reset: Boolean = false, meta: Meta? = null): EntriesState {
    val newHasMore = data.size >= this.size
    return if (reset) {
        this.copy(
            items = data,
            page = 1,
            hasMore = newHasMore,
            isRefreshing = false,
            meta = meta ?: this.meta
        )
    } else {
        this.copy(
            items = this.items + data,
            page = this.page + 1,
            hasMore = newHasMore,
            isLoadingMore = false,
            meta = meta ?: this.meta
        )
    }
}