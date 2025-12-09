package cn.coolbet.orbit.ui.view.entry

import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.manager.EventBus
import cn.coolbet.orbit.manager.Evt
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
import cn.coolbet.orbit.ui.view.home.HomeScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EntryScreenModel @AssistedInject constructor(
    @Assisted private val data: Entry,
    private val entryDao: EntryDao,
    private val session: Session,
    private val eventBus: EventBus,
): ScreenModel {

    @AssistedFactory
    interface Factory: ScreenModelFactory {
        fun create(data: Entry): EntryScreenModel
    }

    private val mutableState = MutableStateFlow(EntryState(entry = data))
    val state: StateFlow<EntryState> = mutableState.asStateFlow()

    init {
        mutableState.update {
            it.copy(
                readerView = data.readableContent.isNotEmpty(),
                readingModeEnabled = data.readableContent.isEmpty() && session.user.autoReaderView,
            )
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

    fun startLoading() {
        mutableState.update { it.copy(isLoadingReadableContent = true) }
    }

    fun updateReadableContent(readableContent: String, leadImageURL: String, summary: String) {
        val entry = state.value.entry
        screenModelScope.launch {
            entryDao.updateReadingModeData(readableContent, leadImageURL, summary, entry.id)
            mutableState.update {
                it.copy(
                    isLoadingReadableContent = false,
                    entry = it.entry.copy(
                        readableContent = readableContent, leadImageURL = leadImageURL,
                        summary = summary
                    ),
                    readerView = readableContent.isNotEmpty(),
                    readingModeEnabled = false
                )
            }
            eventBus.post(Evt.EntryUpdated(state.value.entry))
        }
    }

    fun autoRead() {
        if (!session.user.autoRead) {
            return
        }
        val entry = state.value.entry
        if (!entry.isUnread) {
            return
        }
        screenModelScope.launch {
            try {
                val newEntry = entry.copy(status = EntryStatus.READ)
                Log.i("autoRead", "================== 1. Before updateStatus")

                // ❌ 异常可能发生在这里
                entryDao.updateStatus(newEntry.status, entry.id)

                Log.i("autoRead", "================== 2. After updateStatus (Success)")

                // 后续代码 (事件发送)
                val re = eventBus.post(Evt.EntryUpdated(newEntry))
                val re1 = eventBus.post(Evt.ReadStatusChanged(
                    entry.id,
                    newEntry.isUnread,
                    entry.feedId,
                    entry.feed.folderId
                ))
                Log.i("autoRead", "================== 3. Events posted: $re $re1")

            } catch (e: Exception) {
                // 🌟 关键：捕获并打印异常堆栈，找出真实原因
                Log.e("autoRead", "!!! DATABASE UPDATE FAILED !!!", e)
                // 如果您使用了 TypeConverter，请检查它是否是原因
            }
        }
    }
}

data class EntryState(
    val entry: Entry = Entry.EMPTY,
    val readingModeEnabled: Boolean = false,
    val readerView: Boolean = false,
    val isLoadingReadableContent: Boolean = false
)