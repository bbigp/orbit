package cn.coolbet.orbit.ui.view.addfeed

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

class AddFeedPreviewScreenModel(
    val state: AddFeedPreviewState,
    private val cacheStore: CacheStore,
    private val feedManager: FeedManager,
) : ScreenModel {
    private val _unit = MutableStateFlow(AddFeedPreviewUnit())
    val unit = _unit.asStateFlow()

    private val _effects = MutableSharedFlow<AddFeedPreviewEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    fun onAction(action: AddFeedPreviewAction) {
        when (action) {
            is AddFeedPreviewAction.Subscribe -> subscribe()
        }
    }

    private fun subscribe() {
        if (state.isSubmitting || state.feedId > 0L) return
        screenModelScope.launch {
            state.isSubmitting = true
            try {
                val folderId = resolveSubscribeFolderId()
                val feedId = feedManager.subscribeFeed(state.preview.url, folderId)
                _effects.tryEmit(AddFeedPreviewEffect.Subscribed(feedId))
            } catch (e: Exception) {
                _effects.tryEmit(AddFeedPreviewEffect.Error(e.message ?: "Failed to subscribe feed"))
            } finally {
                state.isSubmitting = false
            }
        }
    }

    private fun resolveSubscribeFolderId(): Long {
        val folders = cacheStore.foldersState.value
        val rootFolderId = Env.settings.rootFolder.value
        val categoryId = folders.find { it.id == rootFolderId }?.id
            ?: folders.firstOrNull()?.id
            ?: 0L
        require(categoryId > 0L) { "No available folder for subscription" }
        return categoryId
    }
}
