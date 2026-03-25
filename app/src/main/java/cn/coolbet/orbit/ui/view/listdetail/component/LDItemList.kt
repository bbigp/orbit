package cn.coolbet.orbit.ui.view.listdetail.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

private sealed interface LDListItem {
    data object Header : LDListItem
    data class Group(val date: String) : LDListItem
    data class EntryRow(val entry: Entry) : LDListItem
}


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

    ObLazyColumn(
        items = listItems,
        pagingState = pagingState,
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
        listState = listState,
        modifier = Modifier.fillMaxSize(),
        onLongPull = onRefresh,
        key = { _, item ->
            when (item) {
                LDListItem.Header -> "ld-header"
                is LDListItem.Group -> "group-${item.date}"
                is LDListItem.EntryRow -> item.entry.id
            }
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
