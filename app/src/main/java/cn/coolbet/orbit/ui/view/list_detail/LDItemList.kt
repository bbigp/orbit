package cn.coolbet.orbit.ui.view.list_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
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
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black50
import cn.coolbet.orbit.ui.view.list_detail.item.LDGroupTitle
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
    scrollState: ListScrollState,
    state: LDItemListState,
    groupedData: Map<String, List<Entry>>
) {
    val actions = LocalListDetailActions.current

    LazyColumn(
        state = scrollState.listState,
        modifier = Modifier.fillMaxSize()
            .pullToRefresh(
                state = scrollState.pullState,
                isRefreshing = state.isRefreshing,
                onRefresh = { scrollState.onRefresh() }
            ),
    ) {
        item(key = "refresh-indicator") {
            RefreshIndicatorItem(
                state = scrollState.pullState,
                isRefreshing = state.isRefreshing,
            )
        }
        item(key = "ld-header") {
            LDHeader(state.meta, modifier = Modifier.graphicsLayer { alpha = 1 - scrollState.progress })
        }

        groupedData.forEach { (date, entries) ->
            if (state.settings.showGroupTitle && date.isNotEmpty()) {
                stickyHeader(key = date) {
                    LDGroupTitle(date = date)
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