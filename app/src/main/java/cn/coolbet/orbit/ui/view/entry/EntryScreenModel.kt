package cn.coolbet.orbit.ui.view.entry

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.manager.EventBus
import cn.coolbet.orbit.manager.Evt
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.model.domain.Entry
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
    @Assisted private val entry: Entry,
    private val entryDao: EntryDao,
    private val session: Session,
    private val eventBus: EventBus,
): ScreenModel {

    @AssistedFactory
    interface Factory: ScreenModelFactory {
        fun create(entry: Entry): EntryScreenModel
    }

    private val mutableState = MutableStateFlow(EntryState(entry = entry))
    val state: StateFlow<EntryState> = mutableState.asStateFlow()

    init {
        mutableState.update {
            it.copy(
                readerView = entry.readableContent.isNotEmpty(),
                readingModeEnabled = entry.readableContent.isEmpty() && session.user.autoReaderView,
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
}

data class EntryState(
    val entry: Entry = Entry.EMPTY,
    val readingModeEnabled: Boolean = false,
    val readerView: Boolean = false,
    val isLoadingReadableContent: Boolean = false
)