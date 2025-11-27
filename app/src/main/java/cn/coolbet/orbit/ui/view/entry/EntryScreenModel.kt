package cn.coolbet.orbit.ui.view.entry

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.ui.view.home.HomeScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EntryScreenModel @AssistedInject constructor(
    @Assisted private val entry: Entry,
): ScreenModel {
    private val mutableState = MutableStateFlow(EntryState(entry = entry))
    val state: StateFlow<EntryState> = mutableState.asStateFlow()

    init {
//        mutableState.update { it.copy(entry) }
    }

    @AssistedFactory
    interface Factory: ScreenModelFactory {
        fun create(entry: Entry): EntryScreenModel
    }


    fun startLoading() {
        mutableState.update { it.copy(isLoadingContent = true) }
    }

    fun updateReadableContent(readableContent: String, leadImageURL: String, summary: String) {
        mutableState.update {
            it.copy(
                isLoadingContent = false,
                entry = it.entry.copy(
                    readableContent = readableContent, leadImageURL = leadImageURL,
                    summary = summary
                )
            )
        }
    }
}

data class EntryState(
    val entry: Entry = Entry.EMPTY,
    val isLoadingContent: Boolean = false
)