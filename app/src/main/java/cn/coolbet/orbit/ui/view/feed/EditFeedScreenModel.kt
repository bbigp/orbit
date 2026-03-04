package cn.coolbet.orbit.ui.view.feed

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.FeedManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Screen model responsible for performing IO for EditFeed flow.
 * All long-running operations run here and update the provided state loading flags.
 */
class EditFeedScreenModel(
    val state: EditFeedState,
    val cacheStore: CacheStore,
    private val feedManager: FeedManager,
) : ScreenModel {
    private val _unit = MutableStateFlow(EditFeedUnit())
    val unit = _unit.asStateFlow()
    private val _effects = MutableSharedFlow<EditFeedEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    init {
        screenModelScope.launch {
            cacheStore.foldersState.collect { folders ->
                _unit.value = _unit.value.copy(folders = folders)
                if (state.feed.id == 0L && state.category.id == 0L) {
                    val rootFolderId = Env.settings.rootFolder.value
                    folders.find { it.id == rootFolderId }?.let { state.updateCategory(it) }
                }
            }
        }
    }

    fun onAction(action: EditFeedAction) {
        when (action) {
            is EditFeedAction.ApplyChanges -> applyChanges()
            is EditFeedAction.Unsubscribe -> unsubscribe()
        }
    }

    private fun applyChanges() {
        if (state.isApplying) return
        screenModelScope.launch {
            state.isApplying = true
            try {
                // TODO: apply title/category changes with `state`.
            } catch (e: Exception) {
                _effects.emit(EditFeedEffect.Error(e.message ?: "Failed to apply feed changes"))
            } finally {
                state.isApplying = false
            }
        }
    }

    private fun unsubscribe() {
        if (state.isUnsubscribing) return
        screenModelScope.launch {
            state.isUnsubscribing = true
            try {
                feedManager.unsubscribeFeed(state.feed.id)
                _effects.emit(EditFeedEffect.Unsubscribed)
            } catch (e: Exception) {
                _effects.emit(EditFeedEffect.Error(e.message ?: "Failed to unsubscribe feed"))
            } finally {
                state.isUnsubscribing = false
            }
        }
    }
}
