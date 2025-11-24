package cn.coolbet.orbit.ui.view.search_entries

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.common.ILoadingState
import cn.coolbet.orbit.dao.SearchDao
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
            mutableState.update { it.copy(isRefreshing = true) }
            val histories = searchDao.getList(meta.metaId.toString())
            mutableState.update { it.copy(histories = histories, isRefreshing = false) }
        }
    }

    fun loadEntries() {

    }

}



data class SearchEntriesState(
    val meta: Meta = Feed.EMPTY,
    val search: String = "",
    val histories: List<String> = emptyList(),
    override val hasMore: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
): ILoadingState