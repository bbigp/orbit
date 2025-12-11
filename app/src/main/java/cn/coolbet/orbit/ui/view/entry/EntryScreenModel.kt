package cn.coolbet.orbit.ui.view.entry

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.manager.EventBus
import cn.coolbet.orbit.manager.Evt
import cn.coolbet.orbit.manager.LocalDataManager
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
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
    private val localDataManager: LocalDataManager,
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

    fun changeStarred() {
        screenModelScope.launch {
            val current = state.value.entry
            val value = current.copy(starred = !current.starred)
            localDataManager.updateFlags(value.id, starred = value.starred)
            mutableState.update { it.copy(entry = value) }
            eventBus.post(Evt.EntryUpdated(value))
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
        eventBus.post(Evt.EntryStatusUpdated(
            entry.id,
            EntryStatus.READ,
            entry.feedId,
            entry.feed.folderId
        ))
    }
}