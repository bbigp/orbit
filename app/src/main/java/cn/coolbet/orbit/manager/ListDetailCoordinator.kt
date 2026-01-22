package cn.coolbet.orbit.manager

import android.util.Log
import cn.coolbet.orbit.common.FreezeableStateWrapper
import cn.coolbet.orbit.common.ILoadingState
import cn.coolbet.orbit.dao.LDSettingsDao
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.domain.update
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.kit.ListLoadMoreState
import cn.coolbet.orbit.ui.view.list_detail.LDItemListState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 协调器
 */
class ListDetailCoordinator(
    val cacheStore: CacheStore,
    val ldSettingsDao: LDSettingsDao,
    val entryManager: EntryManager,
    val eventBus: EventBus,
    appScope: CoroutineScope,
){

    private val stateWrapper: FreezeableStateWrapper<ListDetailState> = FreezeableStateWrapper(appScope,
        ListDetailState())
    val state = stateWrapper.state
    private var previousState: ListDetailState? = null

    fun initData(scope: CoroutineScope, metaId: MetaId, settings: LDSettings? = null, search: String = "") {
        update { it.copy(state = LoadingState.Loading) }
        scope.launch { initData(metaId = metaId, settings = settings, search = search) }
        //搜索页面一定是在列表页面之后的，2个页面公用一份数据管理类，所以只在列表页面建立监听就可以了
        if (search.isEmpty()) {
            eventBus.subscribe<Evt.EntryUpdated>(scope) { event ->
                    modifyEntries(event.entry.id) { event.entry }
                }
                .subscribe<Evt.EntryStatusUpdated>(scope) { event ->
                    modifyEntries(event.entryId) { it.copy(status = event.status) }
                }
        }
    }

    fun refresh(scope: CoroutineScope) {
        val value = state.value
        update { it.copy(items = emptyList()) }
        scope.launch { initData(metaId = value.meta.metaId, settings = value.settings, search = value.search) }
    }

    fun loadMore(scope: CoroutineScope) {
        scope.launch { loadMore() }
    }

    /**
     * 从快照恢复数据
     */
    fun restoreSnapshot() {
        stateWrapper.freeze()
        previousState?.let {
            stateWrapper.setValue(it)
            previousState = null
        }
        Log.i("coordinator", "restoreSnapshot")
    }

    /**
     * 存储快照
     */
    fun captureSnapshot() {
        previousState = stateWrapper.value
        reset()
        Log.i("coordinator", "captureSnapshot")
    }

    fun reset() {
        update { ListDetailState() }
        Log.i("coordinator", "reset")
    }

    fun update(action: (ListDetailState) -> ListDetailState) {
        stateWrapper.update(action)
    }

    fun unfreeze() {
        stateWrapper.unfreeze()
        Log.i("coordinator", "unfreeze")
    }

    fun clear() {
        update { ListDetailState() }
        previousState = null
        Log.i("coordinator", "clear data")
    }


    suspend fun initData(metaId: MetaId, settings: LDSettings? = null, search: String = "") {
        val value = state.value
        if (value.isRefreshing) return
        update { it.copy(isRefreshing = true) }
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
            update {
                it.copy(
                    settings = ldSettings, search = search,
                    items = newData,
                    page = 1, hasMore = newData.size >= it.size,
                    meta = meta,
                    state = if (newData.isEmpty()) LoadingState.Empty else LoadingState.Loaded,
                    isRefreshing = false
                )
            }
        } catch (e: Exception) {
            update { it.copy(isRefreshing = false, state = LoadingState.Error("Post fetch error", e)) }
        }
    }

    suspend fun loadMore() {
        if (!state.value.hasMore) return
        if (state.value.isLoadingMore) return
        update { it.copy(isLoadingMore = true) }
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
            update {
                it.copy(
                    items = it.items + newData,
                    page = it.page + 1, hasMore = newData.size >= it.size,
                    isLoadingMore = false,
                    state = LoadingState.Loaded
                )
            }
        } catch (e: Exception) {
            update { it.copy(isLoadingMore = false) }
            Log.e("BasePagingScreenModel", "加载数据出错.", e)
        }
    }

    fun modifyEntries(id: Long, transform: (Entry) -> Entry) {
        update { currentState ->
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
    override val meta: Meta = Feed.EMPTY,
    val items: List<Entry> = emptyList(),
    val page: Int = 1,
    val size: Int = 20,
    override val hasMore: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val settings: LDSettings = LDSettings.defaultSettings,
    val search: String = "",
    val state: LoadingState = LoadingState.Idle,
): ILoadingState, LDItemListState, ListLoadMoreState

fun ListDetailState.total(): Int {
    return this.items.size
}

sealed class LoadingState {
    object Idle : LoadingState()
    object Loading : LoadingState()
    object Loaded : LoadingState()
    object Empty : LoadingState()
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : LoadingState()
}