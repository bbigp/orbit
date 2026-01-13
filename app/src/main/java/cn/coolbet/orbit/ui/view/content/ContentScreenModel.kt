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
import cn.coolbet.orbit.manager.total
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContentScreenModel @AssistedInject constructor(
    private val entryDao: EntryDao,
    private val localDataManager: LocalDataManager,
    private val session: Session,
    private val eventBus: EventBus,
    private val navigatorState: NavigatorState,
    private val entryManager: EntryManager,
): ScreenModel {

    private val mutableState = MutableStateFlow(ContentState())
    val state: StateFlow<ContentState> = mutableState.asStateFlow()
    val entryStateValue = navigatorState.state.value

    fun loadData(entry: Entry) {
        screenModelScope.launch {
            val index = entryStateValue.items.indexOfFirst { it.id == entry.id }
            if (index != -1 && index == entryStateValue.total() - 1 && entryStateValue.hasMore) { //最后一个 加载数据
                navigatorState.loadMore()
                Log.i("nextEntry", "数据加载完成, 当前 $index  总共: ${entryStateValue.total()} ")
            }
            Log.i("entry-load-data", "entry index: $index")
            mutableState.update {
                ContentState(
                    entry = entry,
                    readerView = entry.readableContent.isNotEmpty(),
                    readingModeEnabled = entry.readableContent.isEmpty() && true,
                    isLoadingReadableContent = false,
                    index = index
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
        val total = entryStateValue.total()
        Log.i("nextEntry", "当前 $currentIndex  总共: $total ")
        if (total == 0 || currentIndex < 0) return null
        if (currentIndex == total - 1) return null//最后一个元素
        return entryStateValue.items[currentIndex + 1]
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
            val entry = entryStateValue.items.find { it.id == id }
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