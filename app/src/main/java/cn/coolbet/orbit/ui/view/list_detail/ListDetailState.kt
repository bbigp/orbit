package cn.coolbet.orbit.ui.view.list_detail

import androidx.compose.runtime.compositionLocalOf
import cn.coolbet.orbit.common.ILoadingState
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.entity.LDSettingKey
import cn.coolbet.orbit.model.entity.LDSettings


val LocalChangeLDSettings = compositionLocalOf<(MetaId, LDSettingKey, Any) -> Unit> { { _, _, _ -> } }

data class ListDetailState(
    val meta: Meta = Feed.EMPTY,
    val items: List<Entry> = emptyList(),
    val page: Int = 1,
    val size: Int = 20,
    override val hasMore: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    val settings: LDSettings = LDSettings.defaultSettings
): ILoadingState

fun ListDetailState.addItems(data: List<Entry>, reset: Boolean = false, meta: Meta? = null): ListDetailState {
    val newHasMore = data.size >= this.size
    return if (reset) {
        this.copy(
            items = data,
            page = 1,
            hasMore = newHasMore,
            isRefreshing = false,
            meta = meta ?: this.meta
        )
    } else {
        this.copy(
            items = this.items + data,
            page = this.page + 1,
            hasMore = newHasMore,
            isLoadingMore = false,
            meta = meta ?: this.meta
        )
    }
}