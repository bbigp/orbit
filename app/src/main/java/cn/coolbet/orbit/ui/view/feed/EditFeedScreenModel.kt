package cn.coolbet.orbit.ui.view.feed

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch

class EditFeedScreenModel(

): StateScreenModel<EditFeedScreenState>(EditFeedScreenState()) {


    fun submit(folderId: Long, feeId: Long) {
        screenModelScope.launch {

        }
    }

}