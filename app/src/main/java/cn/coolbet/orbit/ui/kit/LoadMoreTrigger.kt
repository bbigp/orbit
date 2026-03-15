package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun LoadMoreTrigger(
    listState: LazyListState,
    itemCount: Int,
    pagingState: PagingLoadState,
    prefetchItemCount: Int = 4,
    onLoadMore: () -> Unit,
) {
    LaunchedEffect(listState, itemCount, pagingState, prefetchItemCount) {
        snapshotFlow {
            shouldTriggerLoadMore(
                listState = listState,
                itemCount = itemCount,
                pagingState = pagingState,
                prefetchItemCount = prefetchItemCount
            )
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMore() }
    }
}

private fun shouldTriggerLoadMore(
    listState: LazyListState,
    itemCount: Int,
    pagingState: PagingLoadState,
    prefetchItemCount: Int
): Boolean {
    if (pagingState.isRefreshing || pagingState.isLoadingMore || pagingState.appendError != null || !pagingState.hasMore) return false
    if (itemCount <= 0) return false

    val maxVisibleIndex = listState.layoutInfo.visibleItemsInfo.maxOfOrNull { it.index } ?: -1
    if (maxVisibleIndex < 0) return false

    val triggerIndex = (itemCount - 1 - prefetchItemCount).coerceAtLeast(0)
    return maxVisibleIndex >= triggerIndex
}
