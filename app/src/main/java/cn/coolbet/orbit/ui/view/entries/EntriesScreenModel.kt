package cn.coolbet.orbit.ui.view.entries

import android.util.Log
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.common.BasePagingScreenModel
import cn.coolbet.orbit.common.PageState
import cn.coolbet.orbit.common.addItems
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
): BasePagingScreenModel<Entry, Meta>(initialState = PageState(extra = Feed.EMPTY)) {

    val unreadMapState: StateFlow<Map<String, Int>> = cacheStore.unreadMapState

    init {
        mutableState.update { it.copy(isRefreshing = true) }
    }

    fun refresh(){
        val id = state.value.extra.metaId
        this.clearState()
        loadInitialData(id)
    }

    fun loadInitialData(metaId: MetaId) {
        val value = state.value
        if (value.isRefreshing && value.extra.isNotEmpty) return
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
                val extra = metaDataFlow.first()
                val newData = fetchData(page = 1, size = value.size, extra)
                mutableState.update { it.addItems(newData, reset = true, extra) }
            } catch (e: Exception) {
                mutableState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    override suspend fun fetchData(page: Int, size: Int, extra: Meta): List<Entry> {
        return entryManager.getPage(extra, page = page, size = size)
    }

    fun clearState() {
        mutableState.update { PageState(extra = Feed.EMPTY, isRefreshing = true) }
    }

}