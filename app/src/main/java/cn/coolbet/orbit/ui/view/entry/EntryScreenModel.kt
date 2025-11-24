package cn.coolbet.orbit.ui.view.entry

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.model.domain.Entry
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class EntryScreenModel @AssistedInject constructor(
    @Assisted private val entry: Entry,
): ScreenModel {

    @AssistedFactory
    interface Factory: ScreenModelFactory {
        fun create(entry: Entry): EntryScreenModel
    }
}