package cn.coolbet.orbit.ui.view.list_detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.manager.ListDetailState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LDItemList(
    state: ListDetailState,
    listState: LazyListState,
    progress: Float,
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
            LDHeader(state.meta, modifier = Modifier.graphicsLayer { alpha = 1 - progress })
        }
        items(state.items, key = { it.id }) { item ->
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
        if (state.hasMore) {
            item(key = "loadMoreIndicator") { LoadMoreIndicator() }
        } else {
            item(key = "noMoreIndicator") { NoMoreIndicator() }
        }
    }
}