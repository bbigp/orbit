package cn.coolbet.orbit.ui.view.listdetail

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.dao.LDSettingsDao
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.manager.EventBus
import cn.coolbet.orbit.manager.Evt
import cn.coolbet.orbit.manager.ListDetailCoordinator
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.domain.OpenContentWith
import cn.coolbet.orbit.model.domain.dateLabel
import cn.coolbet.orbit.model.entity.DisplayMode
import cn.coolbet.orbit.model.entity.LDSettingKey
import cn.coolbet.orbit.model.entity.LDSort
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListDetailScreenModel(
    private val metaId: MetaId,
    private val entryManager: EntryManager,
    private val cacheStore: CacheStore,
    private val eventBus: EventBus,
    private val ldSettingsDao: LDSettingsDao,
    val coordinator: ListDetailCoordinator
): ScreenModel {

    val unreadMapState: StateFlow<Map<String, Int>> = cacheStore.unreadMapState

    init {
        coordinator.initData(scope = screenModelScope, metaId = metaId)
    }

    private val itemsFlow = coordinator.state.map { it.items }.distinctUntilChanged()
    private val settingsFlow = coordinator.state.map { it.settings }.distinctUntilChanged()
    @RequiresApi(Build.VERSION_CODES.O)
    val groupedItemsFlow = combine(itemsFlow, settingsFlow) { items, settings ->
        if (settings.showGroupTitle) {
            items.groupBy { it.dateLabel }
        } else {
            mapOf("" to items)
        }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())


    fun refresh() {
        coordinator.refresh(scope = screenModelScope)
    }

    fun nextPage() {
        coordinator.loadMore(scope = screenModelScope)
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

}