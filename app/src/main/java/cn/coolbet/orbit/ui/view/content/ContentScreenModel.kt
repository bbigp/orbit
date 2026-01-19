package cn.coolbet.orbit.ui.view.content

import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.EventBus
import cn.coolbet.orbit.manager.Evt
import cn.coolbet.orbit.manager.ListDetailCoordinator
import cn.coolbet.orbit.manager.LocalDataManager
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.manager.total
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
import cn.coolbet.orbit.model.domain.ReaderPageState
import cn.coolbet.orbit.ui.view.content.extractor.Oeeeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class ContentScreenModel @Inject constructor(
    private val entryDao: EntryDao,
    private val localDataManager: LocalDataManager,
    private val session: Session,
    private val eventBus: EventBus,
    val coordinator: ListDetailCoordinator,
    private val entryManager: EntryManager,
    private val oeeeed: Oeeeed,
    private val appScope: CoroutineScope,
): ScreenModel {

    private val mutableState = MutableStateFlow(ContentState())
    val state: StateFlow<ContentState> = mutableState.asStateFlow()

    init {
        eventBus.subscribe<Evt.EntryUpdated>(screenModelScope) { event ->
            if (state.value.entry.id == event.entry.id) {
                mutableState.update { it.copy(entry = event.entry) }
            }
        }
    }

    fun loadData(entry: Entry) {
        screenModelScope.launch {
            val raw = coordinator.state.value
            val index = raw.items.indexOfFirst { it.id == entry.id }
            if (index != -1 && index == raw.total() - 1 && raw.hasMore) { //最后一个 加载数据
                coordinator.loadMore()
                Log.i("nextEntry", "数据加载完成, 当前 $index  总共: ${raw.total()} ")
            }
            Log.i("entry-load-data", "entry index: $index")
            mutableState.update {
                ContentState(
                    entry = entry,
                    readerModeOpened = entry.readableContent.isNotEmpty(),
                    index = index
                )
            }
            autoRead()
        }
    }

    fun toggleReaderMode(){
        val opened = !mutableState.value.readerModeOpened
        mutableState.update {
            it.copy(
                readerModeOpened = opened,
                entry = it.entry.copy(
                    readerPageState =
                        if (opened && it.entry.readerPageState == ReaderPageState.Idle)
                            ReaderPageState.Fetching
                        else
                            it.entry.readerPageState,
                ),
            )
        }
        val v = state.value
        if (v.entry.readerPageState == ReaderPageState.Fetching) {
            appScope.launch {
                val entry = state.value.entry
                runCatching {
                    val readableDoc = oeeeed.fetchAndExtractContent(entry.url)
                    entryDao.updateReadingModeData(
                        readableDoc.extracted.content,
                        readableDoc.extracted.leadImageUrl,
                        readableDoc.extracted.excerpt ?: "",
                        ReaderPageState.Success,
                        entry.id
                    )
                    val newEntry = entry.copy(
                        readableContent = readableDoc.extracted.content,
                        leadImageURL = readableDoc.extracted.leadImageUrl,
                        summary = readableDoc.extracted.excerpt ?: "",
                        readerPageState = ReaderPageState.Success,
                    )
                    eventBus.post(Evt.EntryUpdated(newEntry))
                }.onFailure { e ->
                    if (e is CancellationException) return@onFailure
                    entryDao.updateReadingModeData("", "", "",
                        ReaderPageState.Failure, entry.id
                    )
                    eventBus.post(Evt.EntryUpdated(entry.copy(
                        readerPageState = ReaderPageState.Failure
                    )))
                }
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
        val raw = coordinator.state.value
        val total = raw.total()
        Log.i("nextEntry", "当前 $currentIndex  总共: $total ")
        if (total == 0 || currentIndex < 0) return null
        if (currentIndex == total - 1) return null//最后一个元素
        return raw.items[currentIndex + 1]
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