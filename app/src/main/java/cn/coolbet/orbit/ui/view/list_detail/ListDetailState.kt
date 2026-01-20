package cn.coolbet.orbit.ui.view.list_detail

import androidx.compose.runtime.compositionLocalOf
import cn.coolbet.orbit.manager.ListDetailState
import cn.coolbet.orbit.manager.LoadingState
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.entity.LDSettingKey


val LocalChangeLDSettings = compositionLocalOf<(MetaId, LDSettingKey, Any) -> Unit> { { _, _, _ -> } }


fun ListDetailState.addItems(data: List<Entry>, reset: Boolean = false, meta: Meta? = null): ListDetailState {
    val newHasMore = data.size >= this.size
    return if (reset) {
        this.copy(
            items = data,
            page = 1,
            hasMore = newHasMore,
            isRefreshing = false,
            meta = meta ?: this.meta,
            state = if (data.isEmpty()) LoadingState.Empty else LoadingState.Loaded
        )
    } else {
        this.copy(
            items = this.items + data,
            page = this.page + 1,
            hasMore = newHasMore,
            isLoadingMore = false,
            meta = meta ?: this.meta,
        )
    }
}

val LocalListDetailActions = compositionLocalOf<ListDetailActions> {
    error("No function provided")
}

interface ListDetailActions {
    fun onRefresh()
    fun loadMore()
    fun toggleRead(entry: Entry)
    fun onBack()
}
