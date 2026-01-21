package cn.coolbet.orbit.ui.view.list_detail

import androidx.compose.runtime.compositionLocalOf
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.entity.LDSettingKey


val LocalChangeLDSettings = compositionLocalOf<(MetaId, LDSettingKey, Any) -> Unit> { { _, _, _ -> } }

val LocalListDetailActions = compositionLocalOf<ListDetailActions> {
    error("No function provided")
}

interface ListDetailActions {
    fun toggleRead(entry: Entry)
    fun onBack()
}
