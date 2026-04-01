package cn.coolbet.orbit.ui.view.addfeed

import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.view.listdetail.component.LDItemListState

internal data class AddFeedPreviewListState(
    override val meta: Meta,
    override val settings: LDSettings = LDSettings.defaultSettings,
) : LDItemListState

