package cn.coolbet.orbit.ui.view.list_detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.ObToastManager
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.view.list_detail.item.LDHeader
import cn.coolbet.orbit.ui.view.list_detail.item.LDRow
import cn.coolbet.orbit.ui.view.list_detail.swipable.NoneStateDefinition
import cn.coolbet.orbit.ui.view.list_detail.swipable.ReadStateDefinition
import cn.coolbet.orbit.ui.view.list_detail.swipable.SwipeWrapper
import cn.coolbet.orbit.ui.view.list_detail.swipable.UnreadStateDefinition
import cn.coolbet.orbit.ui.view.sync.RefreshIndicatorItem

interface LDItemListState {
    val settings: LDSettings
    val isRefreshing: Boolean
    val hasMore: Boolean
    val meta: Meta
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LDItemList(
    listState: LazyListState,
    progress: () -> Float,
    state: LDItemListState,
    groupedData: Map<String, List<Entry>>
) {
    val actions = LocalListDetailActions.current
    val pullState = rememberPullToRefreshState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
            .pullToRefresh(
                state = pullState,
                isRefreshing = state.isRefreshing,
                onRefresh = { actions.onRefresh() }
            ),
    ) {
        item(key = "refresh-indicator") {
            RefreshIndicatorItem(
                state = pullState,
                isRefreshing = state.isRefreshing,
            )
        }
        item(key = "ld-header") {
            LDHeader(state.meta, modifier = Modifier.graphicsLayer { alpha = 1 - progress() })
        }

        groupedData.forEach { (date, entries) ->
            if (state.settings.showGroupTitle && date.isNotEmpty()) {
                stickyHeader(key = date) {
                    Text(date)
                }
            }

            items(entries, key = { it.id }) { item ->
                SwipeWrapper(
                    rightSwipeState = if (item.isUnread) ReadStateDefinition.copy(
                        onClick = {
                            actions.toggleRead(item)
                            ObToastManager.show("Marked as Read")
                        }
                    ) else UnreadStateDefinition.copy(
                        onClick = {
                            actions.toggleRead(item)
                            ObToastManager.show("Marked as Unread")
                        }
                    ),
                    leftSwipeState = NoneStateDefinition
                ) {
                    LDRow(item, state.settings.displayMode, modifier = Modifier.click {
                        NavigatorBus.push(Route.Entry(item, state.settings))
                    })
                }
                SpacerDivider(start = 16.dp, end = 16.dp)
            }
        }

        if (!state.isRefreshing) {
            if (state.hasMore) {
                item(key = "loadMoreIndicator") { LoadMoreIndicator() }
            } else {
                item(key = "noMoreIndicator") { NoMoreIndicator() }
            }
        }
    }
}


@Composable
fun LazyListState.calculateProgress(flyDistance: Dp): () -> Float {
    val density = LocalDensity.current
    val flyDistancePx = with(density) { flyDistance.toPx() }

    // 使用 derivedStateOf 保证只有计算结果变化时才通知 UI
    val progressState = remember(this) {
        derivedStateOf {
            val firstIndex = firstVisibleItemIndex
            val firstOffset = firstVisibleItemScrollOffset

            when {
                firstIndex <= 1 && firstOffset <= 0 -> 0f
                firstIndex > 1 -> 1f
                else -> (firstOffset.toFloat() / flyDistancePx).coerceIn(0f, 1f)

                //因为第一项是 RefreshIndicatorItem 所以listState.firstVisibleItemIndex 必须从1开始算
//                    // 明确：如果是第 0 项且位移为 0，进度必须是 0
//                    firstIndex == 0 && firstOffset <= 0 -> 0f
//                    // 如果已经滚过第一项了，进度必须是 1
//                    firstIndex > 0 -> 1f
//                    // 在第一项内部滚动时的计算
//                    else -> (firstOffset.toFloat() / flyDistancePx).coerceIn(0f, 1f)
            }
        }
    }
    // 返回 Lambda，实现延迟读取
    return { progressState.value }
}