package cn.coolbet.orbit.ui.view.search_entries

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.dao.SearchDao
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.manager.EventBus
import cn.coolbet.orbit.manager.ListDetailCoordinator
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.entity.SearchRecord
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchEntriesScreenModel @AssistedInject constructor(
    @Assisted val meta: Meta,
    private val searchDao: SearchDao,
    private val entryManager: EntryManager,
    private val session: Session,
    val coordinator: ListDetailCoordinator,
    private val eventBus: EventBus,
): ScreenModel {

    @AssistedFactory
    interface Factory: ScreenModelFactory {
        fun create(meta: Meta): SearchEntriesScreenModel
    }

    private val mutableState = MutableStateFlow(SearchState())
    val state = mutableState.asStateFlow()

    init {
        loadSearchList()
        coordinator.captureSnapshot()
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
            delay(300)

            val now = System.currentTimeMillis()
            val record = SearchRecord(userId = state.value.userId, metaId = meta.metaId.toString(),
                word = trimWord, createdAt = now, changedAt = now)
            searchDao.insert(record)
            mutableState.update {
                it.copy(histories = it.histories.plus(trimWord))
            }

            coordinator.initData(metaId = meta.metaId, search = trimWord)
        }
    }

    fun nextPage() {
        coordinator.loadMore(screenModelScope)
    }

    fun deleteHistories() {
        screenModelScope.launch {
            searchDao.deleteAll(metaId = meta.metaId.toString())
            mutableState.update { it.copy(histories = emptySet()) }
        }
    }

}



data class SearchState(
    val userId: Long = 0,
    val histories: Set<String> = emptySet(),
    val count: Int = 0,
    val search: String = "",
)