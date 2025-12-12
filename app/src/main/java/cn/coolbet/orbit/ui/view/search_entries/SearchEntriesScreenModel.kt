package cn.coolbet.orbit.ui.view.search_entries

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.common.ILoadingState
import cn.coolbet.orbit.dao.SearchDao
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.manager.EventBus
import cn.coolbet.orbit.manager.Evt
import cn.coolbet.orbit.manager.NavigatorState
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.replace
import cn.coolbet.orbit.model.entity.SearchRecord
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchEntriesScreenModel @AssistedInject constructor(
    @Assisted val meta: Meta,
    private val searchDao: SearchDao,
    private val entryManager: EntryManager,
    private val session: Session,
    navigatorState: NavigatorState,
    private val eventBus: EventBus,
): ScreenModel {

    @AssistedFactory
    interface Factory: ScreenModelFactory {
        fun create(meta: Meta): SearchEntriesScreenModel
    }

    val mutableState = navigatorState.searchUi
    val state = mutableState.asStateFlow()

    init {
        loadSearchList()
        eventBus
            .subscribe<Evt.EntryUpdated>(screenModelScope) { event ->
                mutableState.update { currentState ->
                    val updatedItems = currentState.items.replace(event.entry)
                    if (updatedItems == currentState.items) {
                        return@update currentState
                    }
                    return@update currentState.copy(items = updatedItems)
                }
            }
            .subscribe<Evt.EntryStatusUpdated>(screenModelScope) { event ->
                mutableState.update { value ->
                    val index = value.items.indexOfFirst { it.id == event.entryId }
                    if (index == -1) {
                        return@update value
                    }
                    val newItems = value.items.toMutableList().apply {
                        this[index] = this[index].copy(status = event.status)
                    }
                    return@update value.copy(items = newItems)
                }
            }
    }

    fun loadSearchList() {
        screenModelScope.launch {
            val histories = searchDao.getList(meta.metaId.toString())
            mutableState.update { it.copy(histories = histories.toSet(), userId = session.user.id) }
        }
    }

    fun input(word: String){
        mutableState.update { it.copy(search = word.trim()) }
    }

    fun load(word: String) {
        screenModelScope.launch {
            val trimWord = word.trim()
            mutableState.update { it.copy(isRefreshing = true, search = trimWord) }
            delay(300)

            val now = System.currentTimeMillis()
            val record = SearchRecord(userId = state.value.userId, metaId = meta.metaId.toString(),
                word = trimWord, createdAt = now, changedAt = now)
            searchDao.insert(record)

            val items = entryManager.getPage(meta, 1, state.value.size, trimWord)
            mutableState.update {
                it.copy(
                    page = 1, items = items,
                    isRefreshing = false, hasMore = items.size >= it.size,
                    histories = it.histories.plus(trimWord)
                )
            }
        }
    }

    fun nextPage() {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoadingMore = true) }
            delay(200)
            val page = state.value.page + 1
            val items = entryManager.getPage(
                meta, page,
                state.value.size,
                state.value.search
            )
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
            mutableState.update { it.copy(histories = emptySet()) }
        }
    }

    fun clearSearchResult() {
        mutableState.update {
            it.copy(
                search = "", page = 0, size = it.size, items = emptyList(),
                hasMore = false, isRefreshing = false, isLoadingMore = false,
            )
        }
    }

    fun onDispose(screenName: String) {
        eventBus.post(Evt.ScreenDisposeRequest(screenName))
    }

}



data class SearchEntriesState(
    val userId: Long = 0,
    val histories: Set<String> = emptySet(),
    val count: Int = 0,
    val meta: Meta = Feed.EMPTY,
    val search: String = "",
    val page: Int = 0,
    val size: Int = 20,
    val items: List<Entry> = emptyList(),
    override val hasMore: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
): ILoadingState