package cn.coolbet.orbit.manager

import android.util.Log
import cn.coolbet.orbit.common.ILoadingState
import cn.coolbet.orbit.dao.LDSettingsDao
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.domain.update
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.view.list_detail.addItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigatorState @Inject constructor(
    val cacheStore: CacheStore,
    val ldSettingsDao: LDSettingsDao,
    val entryManager: EntryManager,
    val eventBus: EventBus,
    appScope: CoroutineScope,
){

    // 原始的数据流
    val internalState = MutableStateFlow(ListDetailState())

    // 控制是否锁定的信号
    private val isFrozen = MutableStateFlow(false)

    // 暴露给 UI 的流：当 isFrozen 为 true 时，停止向下游发射新值
    val state: StateFlow<ListDetailState> = internalState
        .combine(isFrozen) { value, frozen -> value to frozen }
        .scan(ListDetailState()) { lastEmitted, (currentReal, frozen) ->
            // 如果处于锁定状态，拦截新数据，返回上一次发出的旧数据
            if (frozen) lastEmitted else currentReal
        }
        .stateIn(
            scope = appScope,
            started = SharingStarted.Eagerly, // 立即启动
            initialValue = ListDetailState()
        )

//    val state: MutableStateFlow<ListDetailState> = MutableStateFlow(ListDetailState())
    private var previousState: ListDetailState? = null

    fun initData(scope: CoroutineScope, metaId: MetaId, settings: LDSettings? = null, search: String = "") {
        scope.launch { initData(metaId = metaId, settings = settings, search = search) }
        if (search.isEmpty()) {
            eventBus.subscribe<Evt.EntryUpdated>(scope) { event ->
                    modifyEntries(event.entry.id) { event.entry }
                }
                .subscribe<Evt.EntryStatusUpdated>(scope) { event ->
                    modifyEntries(event.entryId) { it.copy(status = event.status) }
                }
        }
    }

    fun loadMore(scope: CoroutineScope) {
        scope.launch { loadMore() }
    }

    fun restoreState() {
        isFrozen.value = true
        previousState?.let {
            internalState.value = it
            previousState = null
        }
    }

    fun saveState() {
        previousState = state.value
        internalState.update { ListDetailState() }
    }

    fun unfreeze() {
        isFrozen.value = false
    }

    fun dispose() {
        internalState.update { ListDetailState() }
        previousState = null
    }


    suspend fun initData(metaId: MetaId, settings: LDSettings? = null, search: String = "") {
        val value = state.value
        if (value.isRefreshing) return
        internalState.update { it.copy(isRefreshing = true) }
        val metaDataFlow: Flow<Meta> = when {
            metaId.isFeed -> cacheStore.flowFeed(metaId.id)
            metaId.isFolder -> cacheStore.flowFolder(metaId.id)
            else -> {
                throw IllegalStateException("MetaId type is neither Feed nor Folder.")
            }
        }
        delay(500)
        try {
            val meta = metaDataFlow.first()
            val ldSettings = settings ?: ldSettingsDao.get(metaId.toString()) ?: LDSettings.defaultSettings
            val newData = entryManager.getPage(
                query = ListDetailQuery(meta = meta, settings = ldSettings, search = search),
                page = 1,
                size = value.size
            )
            internalState.update {
                it.copy(settings = ldSettings, search = search)
                    .addItems(newData, reset = true, meta)
            }
        } catch (e: Exception) {
            internalState.update { it.copy(isRefreshing = false) }
        }
    }

    suspend fun loadMore() {
        if (!state.value.hasMore) return
        if (state.value.isLoadingMore) return
        internalState.update { it.copy(isLoadingMore = true) }
        try {
            val newData = entryManager.getPage(
                query = ListDetailQuery(
                    meta = state.value.meta,
                    settings = state.value.settings,
                    search = state.value.search
                ),
                page = state.value.page + 1,
                size = state.value.size
            )
            delay(200)
            internalState.update { it.addItems(newData) }
        } catch (e: Exception) {
            internalState.update { it.copy(isLoadingMore = false) }
            Log.e("BasePagingScreenModel", "加载数据出错.", e)
        }
    }

    fun modifyEntries(id: Long, transform: (Entry) -> Entry) {
        internalState.update { currentState ->
            val updatedItems = currentState.items.update(id, transform)

            if (updatedItems === currentState.items) {
                currentState
            } else {
                currentState.copy(items = updatedItems)
            }
        }
        previousState = previousState?.let { oldState ->
            val newList = oldState.items.update(id, transform)
            if (newList === oldState.items) oldState else oldState.copy(items = newList)
        }
    }
}

data class ListDetailState(
    val meta: Meta = Feed.EMPTY,
    val items: List<Entry> = emptyList(),
    val page: Int = 1,
    val size: Int = 20,
    override val hasMore: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    val settings: LDSettings = LDSettings.defaultSettings,
    val search: String = "",
): ILoadingState

fun ListDetailState.total(): Int {
    return this.items.size
}