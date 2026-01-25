package cn.coolbet.orbit.ui.view.listdetail.setting

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.dao.LDSettingsDao
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.manager.ListDetailCoordinator
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.domain.OpenContentWith
import cn.coolbet.orbit.model.entity.DisplayMode
import cn.coolbet.orbit.model.entity.LDSettingKey
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.model.entity.LDSort
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ListDetailSettingScreenModel(
    private val ldSettingsDao: LDSettingsDao,
    val coordinator: ListDetailCoordinator,
    val cacheStore: CacheStore
): ScreenModel {

    fun changeLDSettings(metaId: MetaId, key: LDSettingKey, value: Any) {
        var unreadOnly: Boolean? = null
        var sortOrder: LDSort? = null
        var showGroupTitle: Boolean? = null
        var displayMode: DisplayMode? = null
        var autoReaderView: Boolean? = null
        var openContentWith: OpenContentWith? = null
        when(key) {
            LDSettingKey.UnreadOnly -> unreadOnly = value as Boolean
            LDSettingKey.SortOrder -> sortOrder = value as LDSort
            LDSettingKey.ShowGroupTitle -> showGroupTitle = value as Boolean
            LDSettingKey.DisPlayMode -> displayMode = value as DisplayMode
            LDSettingKey.AutoReaderView -> autoReaderView = value as Boolean
            LDSettingKey.OpenContentWith -> openContentWith = value as OpenContentWith
        }
        screenModelScope.launch {
            val updated = ldSettingsDao.update(
                metaId, sortOrder = sortOrder, unreadOnly = unreadOnly,
                showGroupTitle = showGroupTitle, displayMode = displayMode,
                autoReaderView = autoReaderView, openContentWith = openContentWith
            )
            if (unreadOnly != null) {
                coordinator.initData(metaId = metaId, settings = updated)
            } else {
                coordinator.update { it.copy(settings = updated) }
            }
        }
    }

}

