package cn.coolbet.orbit.ui.view.listdetail.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.kit.ListScrollState
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.ObToastManager
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.view.listdetail.LocalListDetailActions
import cn.coolbet.orbit.ui.view.listdetail.TwoStagePullRefreshLayout
import cn.coolbet.orbit.ui.view.listdetail.component.item.LDGroupTitle
import cn.coolbet.orbit.ui.view.listdetail.component.item.LDHeader
import cn.coolbet.orbit.ui.view.listdetail.component.item.LDRow

interface LDItemListState {
    val settings: LDSettings
    val isRefreshing: Boolean
    val hasMore: Boolean
    val meta: Meta
}


@Composable
fun LDItemList(
    scrollState: ListScrollState,
    state: LDItemListState,
    groupedData: Map<String, List<Entry>>,
    enablePullToRefresh: Boolean = true,
    enableSwipe: Boolean = true,
) {
    val actions = LocalListDetailActions.current

    val listContent: @Composable () -> Unit = {
        LazyColumn(
            state = scrollState.listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            item(key = "ld-header") {
                LDHeader(
                    meta = state.meta,
                    modifier = Modifier.graphicsLayer { alpha = 1 - scrollState.progress },
                )
            }

            groupedData.forEach { (date, entries) ->
                if (state.settings.showGroupTitle && date.isNotEmpty()) {
                    item(key = "group-$date") {
                        LDGroupTitle(date = date)
                    }
                }

                items(entries, key = { it.id }) { item ->
                    val rowContent: @Composable () -> Unit = {
                        LDRow(item, state.settings.displayMode, modifier = Modifier.click {
                            NavigatorBus.push(Route.Entry(item, state.settings))
                        })
                    }
                    if (enableSwipe) {
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
                            leftSwipeState = NoneStateDefinition,
                            content = rowContent
                        )
                    } else {
                        rowContent()
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

    if (enablePullToRefresh) {
        TwoStagePullRefreshLayout(
            isRefreshing = state.isRefreshing,
            onRefresh = { scrollState.onRefresh() },
            secondStageAction = { scrollState.onRefresh() },
            canPullDown = {
                scrollState.listState.firstVisibleItemIndex == 0 &&
                    scrollState.listState.firstVisibleItemScrollOffset == 0
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            listContent()
        }
    } else {
        listContent()
    }
}
