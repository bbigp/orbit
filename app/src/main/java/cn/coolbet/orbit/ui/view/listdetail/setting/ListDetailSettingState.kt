package cn.coolbet.orbit.ui.view.listdetail.setting

import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.entity.LDSettings

data class ListDetailSettingState(
    val feed: Feed = Feed.EMPTY,
    val settings: LDSettings = LDSettings.defaultSettings
)