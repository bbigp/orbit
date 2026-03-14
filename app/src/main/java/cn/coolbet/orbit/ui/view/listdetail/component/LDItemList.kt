package cn.coolbet.orbit.ui.view.listdetail.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
import cn.coolbet.orbit.ui.kit.PagingLazyColumn
import cn.coolbet.orbit.ui.kit.PagingResult
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.kit.rememberPagingState
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

private sealed interface LDListItem {
    data object Header : LDListItem
    data class Group(val date: String) : LDListItem
    data class EntryRow(val entry: Entry) : LDListItem
}


@Composable
fun LDItemList(
    listState: LazyListState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    state: LDItemListState,
    groupedData: Map<String, List<Entry>>,
    enablePullToRefresh: Boolean = true,
    enableSwipe: Boolean = true,
) {
    val actions = LocalListDetailActions.current
    val pagingState = rememberPagingState(initialHasMoreData = state.hasMore)

    LaunchedEffect(state.hasMore) {
        pagingState.reset(page = pagingState.currentPage, hasMore = state.hasMore)
    }
    LaunchedEffect(state.isRefreshing) {
        if (state.isRefreshing) {
            pagingState.reset(page = 1, hasMore = state.hasMore)
        }
    }

    val listItems = remember(groupedData, state.settings.showGroupTitle, state.meta.id) {
        buildList {
            add(LDListItem.Header)
            groupedData.forEach { (date, entries) ->
                if (state.settings.showGroupTitle && date.isNotEmpty()) {
                    add(LDListItem.Group(date))
                }
                entries.forEach { add(LDListItem.EntryRow(it)) }
            }
        }
    }

    val listContent: @Composable () -> Unit = {
        PagingLazyColumn(
            items = listItems,
            pagingState = pagingState,
            onLoadMore = {
                onLoadMore()
                if (state.hasMore) PagingResult.HasMoreData else PagingResult.NoMoreData
            },
            listState = listState,
            modifier = Modifier.fillMaxSize(),
            key = { _, item ->
                when (item) {
                    LDListItem.Header -> "ld-header"
                    is LDListItem.Group -> "group-${item.date}"
                    is LDListItem.EntryRow -> item.entry.id
                }
            },
            loadingFooter = {
                if (!state.isRefreshing) LoadMoreIndicator()
            },
            endFooter = {
                if (!state.isRefreshing) NoMoreIndicator()
            },
        ) { listItem ->
            when (listItem) {
                LDListItem.Header -> LDHeader(meta = state.meta)
                is LDListItem.Group -> LDGroupTitle(date = listItem.date)
                is LDListItem.EntryRow -> {
                    val item = listItem.entry
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
        }
    }

    if (enablePullToRefresh) {
        TwoStagePullRefreshLayout(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            secondStageAction = onRefresh,
            canPullDown = {
                listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            listContent()
        }
    } else {
        listContent()
    }
}
