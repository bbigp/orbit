package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import cn.coolbet.orbit.common.ILoadingState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun InfiniteScrollHandler(
    listState: LazyListState,
    stateFlow: StateFlow<ILoadingState>,
    onLoadMore: () -> Unit,
    buffer: Int = 4 // 距离列表底部多少项时触发加载
) {
    // 使用 rememberUpdatedState 确保 LaunchedEffect 总是使用最新的 onLoadMore 引用，
    // 即使外部函数引用发生变化，也不会重启 LaunchedEffect。
    val onLoadMoreAction = rememberUpdatedState(onLoadMore)
    val state by stateFlow.collectAsState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                if (!state.hasMore || state.isLoadingMore || state.isRefreshing) {
                    return@collect
                }

                val totalItemsCount = layoutInfo.totalItemsCount
                if (totalItemsCount == 0) {
                    return@collect
                }

                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
                if (lastVisibleItemIndex == null) {
                    return@collect
                }

                val itemsRemaining = totalItemsCount - (lastVisibleItemIndex + 1)
                if (itemsRemaining <= buffer) {
                    onLoadMoreAction.value()
                }
            }
    }
}