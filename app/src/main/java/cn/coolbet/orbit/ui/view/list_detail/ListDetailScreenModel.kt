package cn.coolbet.orbit.ui.view.list_detail

import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cn.coolbet.orbit.dao.LDSettingsDao
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.manager.EventBus
import cn.coolbet.orbit.manager.Evt
import cn.coolbet.orbit.manager.NavigatorState
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.entity.DisplayMode
import cn.coolbet.orbit.model.entity.LDSettingKey
import cn.coolbet.orbit.model.entity.LDSort
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListDetailScreenModel @AssistedInject constructor(
    @Assisted private val metaId: MetaId,
    private val entryManager: EntryManager,
    private val cacheStore: CacheStore,
    private val eventBus: EventBus,
    private val ldSettingsDao: LDSettingsDao,
    private val navigatorState: NavigatorState,
): ScreenModel {

    @AssistedFactory
    interface Factory: ScreenModelFactory {
        fun create(metaId: MetaId): ListDetailScreenModel
    }

    val state = navigatorState.state
    val unreadMapState: StateFlow<Map<String, Int>> = cacheStore.unreadMapState

    init {
        loadInitialData()
    }


    fun changeLDSettings(metaId: MetaId, key: LDSettingKey, value: Any) {
        var unreadOnly: Boolean? = null
        var sortOrder: LDSort? = null
        var showGroupTitle: Boolean? = null
        var displayMode: DisplayMode? = null
        var autoReaderView: Boolean? = null
        when(key) {
            LDSettingKey.UnreadOnly -> unreadOnly = value as Boolean
            LDSettingKey.SortOrder -> sortOrder = value as LDSort
            LDSettingKey.ShowGroupTitle -> showGroupTitle = value as Boolean
            LDSettingKey.DisPlayMode -> displayMode = value as DisplayMode
            LDSettingKey.AutoReaderView -> autoReaderView = value as Boolean
        }
        screenModelScope.launch {
            val updated = ldSettingsDao.update(
                metaId, sortOrder = sortOrder, unreadOnly = unreadOnly,
                showGroupTitle = showGroupTitle, displayMode = displayMode,
                autoReaderView = autoReaderView,
            )
            if (unreadOnly != null) {
                navigatorState.initData(metaId = metaId, settings = updated)
            } else {
                navigatorState.internalState.update { it.copy(settings = updated) }
            }
        }
    }


    fun loadInitialData() {
        navigatorState.initData(scope = screenModelScope, metaId = metaId)
    }

    fun nextPage() {
        navigatorState.loadMore(scope = screenModelScope)
    }

    fun toggleReadStatus(entry: Entry) {
        Log.i("eventbus", "toggleReadStatus EntryStatusUpdated")
        eventBus.post(Evt.EntryStatusUpdated(
            entry.id,
            if (entry.isUnread) EntryStatus.READ else EntryStatus.UNREAD,
            entry.feedId,
            entry.feed.folderId
        ))
    }

    fun unfreeze() {
        Log.i("entries", "unfreeze")
        navigatorState.unfreeze()
    }

}