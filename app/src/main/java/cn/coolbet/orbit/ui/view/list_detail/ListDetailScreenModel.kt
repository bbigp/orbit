package cn.coolbet.orbit.ui.view.list_detail

import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.dao.LDSettingsDao
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.manager.EventBus
import cn.coolbet.orbit.manager.Evt
import cn.coolbet.orbit.manager.NavigatorState
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.domain.replace
import cn.coolbet.orbit.model.entity.LDSettings
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.indexOfFirst
import kotlin.collections.toMutableList

class ListDetailScreenModel @AssistedInject constructor(
    @Assisted private val metaId: MetaId,
    private val entryManager: EntryManager,
    private val cacheStore: CacheStore,
    private val eventBus: EventBus,
    private val ldSettingsDao: LDSettingsDao,
    navigatorState: NavigatorState,
): ScreenModel {

    @AssistedFactory
    interface Factory: ScreenModelFactory {
        fun create(metaId: MetaId): ListDetailScreenModel
    }

    val mutableState = navigatorState.entriesUi
    val state = mutableState.asStateFlow()
    val unreadMapState: StateFlow<Map<String, Int>> = cacheStore.unreadMapState

    init {
        loadInitialData()
        eventBus
            .subscribe<Evt.EntryUpdated>(screenModelScope) { event ->
                replace(event.entry)
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

    fun replace(entry: Entry) {
        mutableState.update { currentState ->
            val updatedItems = currentState.items.replace(entry)
            if (updatedItems == currentState.items) {
                return@update currentState
            }
            return@update currentState.copy(items = updatedItems)
        }
    }


    fun loadInitialData() {
        val value = state.value
        if (value.isRefreshing) return
        mutableState.update { it.copy(isRefreshing = true) }
        val metaDataFlow: Flow<Meta> = when {
            metaId.isFeed -> cacheStore.flowFeed(metaId.id)
            metaId.isFolder -> cacheStore.flowFolder(metaId.id)
            else -> {
                throw IllegalStateException("MetaId type is neither Feed nor Folder.")
            }
        }
        screenModelScope.launch {
            delay(500)
            try {
                val meta = metaDataFlow.first()
                val settings = ldSettingsDao.get(metaId.toString()) ?: LDSettings.defaultSettings
                val newData = entryManager.getPage(meta, page = 1, size = value.size)
                mutableState.update {
                    it.copy(settings = settings).addItems(newData, reset = true, meta)
                }
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

    fun toggleReadStatus(entry: Entry) {
        Log.i("eventbus", "toggleReadStatus EntryStatusUpdated")
        eventBus.post(Evt.EntryStatusUpdated(
            entry.id,
            if (entry.isUnread) EntryStatus.READ else EntryStatus.UNREAD,
            entry.feedId,
            entry.feed.folderId
        ))
    }

    fun onDispose(screenName: String) {
        Log.i("entries", "clear state")
        eventBus.post(Evt.ScreenDisposeRequest(screenName))
    }

}