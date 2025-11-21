package cn.coolbet.orbit.ui.view.entries

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.common.BasePagingScreenModel
import cn.coolbet.orbit.common.PageState
import cn.coolbet.orbit.common.addItems
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EntriesScreenModel @AssistedInject constructor(
    @Assisted val metaId: MetaId,
    private val entryManager: EntryManager,
    private val cacheStore: CacheStore,
): BasePagingScreenModel<Entry, Meta>(initialState = PageState(extra = Feed.EMPTY)) {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(metaId: MetaId): EntriesScreenModel
    }

    init {
        loadInitialData()
    }

    override fun loadInitialData() {
        val metaDataFlow: Flow<Meta> = when {
            metaId.isFeed -> cacheStore.flowFeed(metaId.id)
            metaId.isFolder -> cacheStore.flowFolder(metaId.id)
            else -> {
                throw IllegalStateException("MetaId type is neither Feed nor Folder.")
            }
        }
        screenModelScope.launch {
            if (state.value.isRefreshing) return@launch
            mutableState.update { it.copy(isRefreshing = true) }
            try {
                mutableState.update { it.copy(extra = metaDataFlow.first()) }
                val newData = fetchData(page = 1, size = state.value.size)
                mutableState.update { it.addItems(newData, reset = true) }
            } catch (e: Exception) {
                mutableState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    override suspend fun fetchData(page: Int, size: Int): List<Entry> {
        return entryManager.getPage(state.value.extra, page = page, size = size)
    }

}