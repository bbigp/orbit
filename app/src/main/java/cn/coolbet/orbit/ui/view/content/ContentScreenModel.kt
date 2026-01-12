package cn.coolbet.orbit.ui.view.content

import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.EventBus
import cn.coolbet.orbit.manager.Evt
import cn.coolbet.orbit.manager.LocalDataManager
import cn.coolbet.orbit.manager.NavigatorState
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
import cn.coolbet.orbit.ui.view.list_detail.addItems
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.plus

class ContentScreenModel @AssistedInject constructor(
    @Assisted private val queryContext: QueryContext,
    private val entryDao: EntryDao,
    private val localDataManager: LocalDataManager,
    private val session: Session,
    private val eventBus: EventBus,
    private val navigatorState: NavigatorState,
    private val entryManager: EntryManager,
): ScreenModel {

    @AssistedFactory
    interface Factory: ScreenModelFactory {
        fun create(queryContext: QueryContext): ContentScreenModel
    }

    private val mutableState = MutableStateFlow(ContentState())
    val state: StateFlow<ContentState> = mutableState.asStateFlow()

    private val accessor: PagingStateAccessor
        get() = when (queryContext.page) {
            "normal" -> object : PagingStateAccessor {
                override val items: List<Entry>
                    get() = navigatorState.entriesUi.value.items
                override val total: Int
                    get() = navigatorState.entriesUi.value.items.size
                override val hasMore: Boolean
                    get() = navigatorState.entriesUi.value.hasMore
                override suspend fun nextPage() {
                    val data = navigatorState.entriesUi.value
                    val list = entryManager.getPage(data.meta, page = data.page + 1, size = data.size)
                    navigatorState.entriesUi.update { it.addItems(list) }
                }
                override fun indexOfFirst(item: Entry): Int {
                    return navigatorState.entriesUi.value.items.indexOfFirst { it.id == item.id }
                }
                override fun entry(id: Long): Entry? {
                    return navigatorState.entriesUi.value.items.find { it.id == id }
                }
            }
            "search" -> object : PagingStateAccessor {
                override val items: List<Entry>
                    get() = navigatorState.searchUi.value.items
                override val total: Int
                    get() = navigatorState.searchUi.value.items.size
                override val hasMore: Boolean
                    get() = navigatorState.searchUi.value.hasMore
                override suspend fun nextPage() {
                    val data = navigatorState.searchUi.value
                    val page = data.page + 1
                    val items = entryManager.getPage(data.meta, page, data.size,
                        data.search
                    )
                    navigatorState.searchUi.update {
                        it.copy(
                            page = page, items = it.items + items,
                            hasMore = items.size >= it.size
                        )
                    }
                }
                override fun indexOfFirst(item: Entry): Int {
                    return navigatorState.searchUi.value.items.indexOfFirst { it.id == item.id }
                }
                override fun entry(id: Long): Entry? {
                    return navigatorState.searchUi.value.items.find { it.id == id }
                }
            }
            else -> throw IllegalStateException("Unknown page context: ${queryContext.page}")
        }

    fun loadData(entry: Entry, index: Int? = null) {
        screenModelScope.launch {
            val currentIndex = index ?: accessor.indexOfFirst(entry)
            if (currentIndex != -1 && currentIndex == accessor.total - 1 && accessor.hasMore) { //最后一个 加载数据
                accessor.nextPage()
                Log.i("nextEntry", "数据加载完成, 当前 $currentIndex  总共: ${accessor.total} ")
            }
            Log.i("entry-load-data", "entry index: $currentIndex")
            mutableState.update {
                ContentState(
                    entry = entry,
                    readerView = entry.readableContent.isNotEmpty(),
                    readingModeEnabled = entry.readableContent.isEmpty() && true,
                    isLoadingReadableContent = false,
                    index = currentIndex
                )
            }
            autoRead()
        }
    }

    fun changeDisplayMode(){
        mutableState.update {
            if (!it.readerView && it.entry.readableContent.isEmpty()) {
                it.copy(readerView = true, isLoadingReadableContent = true)
            } else {
                it.copy(readerView = !it.readerView)
            }
        }
    }

    fun changeStarred() {
        screenModelScope.launch {
            val current = state.value.entry
            val value = current.copy(starred = !current.starred)
            localDataManager.updateFlags(value.id, starred = value.starred)
            mutableState.update { it.copy(entry = value) }
            eventBus.post(Evt.EntryUpdated(value))
        }
    }

    fun nextEntry(): Entry? {
        val currentIndex = state.value.index
        val total = accessor.total
        Log.i("nextEntry", "当前 $currentIndex  总共: $total ")
        if (total == 0 || currentIndex < 0) return null
        if (currentIndex == total - 1) return null//最后一个元素
        return accessor.items[currentIndex + 1]
    }

    fun startLoading() {
        mutableState.update { it.copy(isLoadingReadableContent = true) }
    }

    fun updateReadableContent(readableContent: String, leadImageURL: String, summary: String, id: Long) {
        if (readableContent == "<div></div>") {
            return
        }
        screenModelScope.launch {
            entryDao.updateReadingModeData(readableContent, leadImageURL, summary, id)
            val entry = accessor.entry(id)
            if (entry == null) {
                return@launch
            }
            val newEntry = entry.copy(
                readableContent = readableContent, leadImageURL = leadImageURL,
                summary = summary
            )
            mutableState.update {
                if (it.entry.id != id) {
                    return@update it
                }
                it.copy(
                    isLoadingReadableContent = false,
                    entry = newEntry,
                    readerView = newEntry.readableContent.isNotEmpty(),
                    readingModeEnabled = false
                )
            }
            eventBus.post(Evt.EntryUpdated(newEntry))
        }
    }

    fun autoRead() {
        if (!Env.settings.autoRead.value) {
            return
        }
        val entry = state.value.entry
        if (!entry.isUnread) {
            return
        }
        eventBus.post(Evt.EntryStatusUpdated(
            entry.id,
            EntryStatus.READ,
            entry.feedId,
            entry.feed.folderId
        ))
    }
}