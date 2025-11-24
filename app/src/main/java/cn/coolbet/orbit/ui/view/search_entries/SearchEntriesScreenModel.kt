package cn.coolbet.orbit.ui.view.search_entries

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.common.ILoadingState
import cn.coolbet.orbit.dao.SearchDao
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchEntriesScreenModel @AssistedInject constructor(
    @Assisted val meta: Meta,
    private val searchDao: SearchDao,
    private val entryManager: EntryManager,
): StateScreenModel<SearchEntriesState>(initialState = SearchEntriesState()) {

    @AssistedFactory
    interface Factory: ScreenModelFactory {
        fun create(meta: Meta): SearchEntriesScreenModel
    }

    init {
        loadSearchList()
    }

    fun loadSearchList() {
        screenModelScope.launch {
            val histories = searchDao.getList(meta.metaId.toString())
            mutableState.update { it.copy(histories = histories) }
        }
    }

    fun load(word: String) {
        screenModelScope.launch {
            mutableState.update { it.copy(isRefreshing = true, search = word) }
            val items = entryManager.getPage(meta, 1, state.value.size, word)
            mutableState.update {
                it.copy(
                    page = 1, items = items,
                    isRefreshing = false, hasMore = items.size >= it.size
                )
            }
        }
    }

    fun nextPage() {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoadingMore = true) }
            val page = state.value.page + 1
            val items = entryManager.getPage(meta, page, state.value.size, state.value.search)
            mutableState.update {
                it.copy(
                    page = page, items = it.items + items,
                    isLoadingMore = false, hasMore = items.size >= it.size
                )
            }

        }
    }

    fun deleteHistories() {
        screenModelScope.launch {
            searchDao.deleteAll(metaId = meta.metaId.toString())
        }
    }

    fun clearSearchResult() {
        mutableState.update {
            it.copy(
                search = "", page = 1, size = it.size, items = emptyList(),
                hasMore = false, isRefreshing = false, isLoadingMore = false,
            )
        }
    }

}



data class SearchEntriesState(
    val meta: Meta = Feed.EMPTY,
    val search: String = "",
    val histories: List<String> = emptyList(),
    val page: Int = 1,
    val size: Int = 20,
    val items: List<Entry> = emptyList(),
    override val hasMore: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
): ILoadingState