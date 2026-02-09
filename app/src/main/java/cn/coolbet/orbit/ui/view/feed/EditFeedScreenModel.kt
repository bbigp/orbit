package cn.coolbet.orbit.ui.view.feed

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.manager.CacheStore
import kotlinx.coroutines.launch

/**
 * Screen model responsible for performing IO for EditFeed flow.
 * All long-running operations run here and update the provided state loading flags.
 */
class EditFeedScreenModel(
    val state: EditFeedState,
    val content: EditFeedContent,
    val cacheStore: CacheStore,
) : ScreenModel {

    fun applyChanges() {
        if (!state.isModified || state.isApplying) return
        screenModelScope.launch {
            state.isApplying = true
            try {
            } finally {
                state.isApplying = false
            }
        }
    }

    fun unsubscribe() {
        if (state.isUnsubscribing) return
        screenModelScope.launch {
            state.isUnsubscribing = true
            try {
            } finally {
                state.isUnsubscribing = false
            }
        }
    }
}