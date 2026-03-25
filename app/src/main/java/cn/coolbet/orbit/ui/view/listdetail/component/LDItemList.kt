package cn.coolbet.orbit.ui.view.listdetail.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.kit.ObToastManager
import cn.coolbet.orbit.ui.kit.PagingLoadState
import cn.coolbet.orbit.ui.kit.ObLazyColumn
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.view.listdetail.LocalListDetailActions
import cn.coolbet.orbit.ui.view.listdetail.component.item.LDGroupTitle
import cn.coolbet.orbit.ui.view.listdetail.component.item.LDHeader
import cn.coolbet.orbit.ui.view.listdetail.component.item.LDRow

interface LDItemListState {
    val settings: LDSettings
    val meta: Meta
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LDItemList(
    listState: LazyListState,
    onRefresh: (() -> Unit)? = null,
    onLoadMore: (() -> Unit)? = null,
    state: LDItemListState,
    pagingState: PagingLoadState,
    groupedData: Map<String, List<Entry>>,
    enableSwipe: Boolean = true,
) {
    val actions = LocalListDetailActions.current

    val itemCount = 1 + groupedData.values.sumOf { it.size } +
        if (state.settings.showGroupTitle) groupedData.keys.count { it.isNotEmpty() } else 0

    ObLazyColumn(
        itemCount = itemCount,
        pagingState = pagingState,
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
        listState = listState,
        modifier = Modifier.fillMaxSize(),
        onLongPull = onRefresh,
    ) {
        item(key = "ld-header") {
            LDHeader(meta = state.meta)
        }

        groupedData.forEach { (date, entries) ->
            if (state.settings.showGroupTitle && date.isNotEmpty()) {
                stickyHeader(key = "group-$date") {
                    LDGroupTitle(date = date)
                }
            }

            entries.forEach { item ->
                item(key = item.id) {
                    val rowContent: @Composable () -> Unit = {
                        LDRow(item, state.settings.displayMode, modifier = Modifier.click {
                            NavigatorBus.push(Route.Entry(item, state.settings))
                        })
                    }
                    if (enableSwipe) {
                        SwipeActionItem(
                            endAction = if (item.isUnread) MarkReadSwipeAction.copy(
                                onTrigger = {
                                    actions.toggleRead(item)
                                    ObToastManager.show("Marked as Read")
                                }
                            ) else MarkUnreadSwipeAction.copy(
                                onTrigger = {
                                    actions.toggleRead(item)
                                    ObToastManager.show("Marked as Unread")
                                }
                            ),
                            startAction = DisabledSwipeAction,
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
}
