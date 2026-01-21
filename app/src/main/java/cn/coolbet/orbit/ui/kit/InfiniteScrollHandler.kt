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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

interface ListLoadMoreState {
    val hasMore: Boolean
    val isLoadingMore: Boolean
    val isRefreshing: Boolean
}

@Composable
fun ListLoadMoreHandler(
    scrollState: ListScrollState,
    state: ListLoadMoreState,
    enabled: Boolean = true,
    buffer: Int = 4
) {
    if (!enabled) return
    // 使用 rememberUpdatedState 确保 Lambda 内部始终能拿到最新的 onLoadMore 引用
    val currentOnLoadMore by rememberUpdatedState(scrollState.onLoadMore)

    LaunchedEffect(scrollState.listState) {
        snapshotFlow {
            val layoutInfo = scrollState.listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

            // 将你的逻辑封装进 flow 的发射条件中
            val canTriggerPositionally = totalItemsCount > 0 &&
                    (totalItemsCount - (lastVisibleItemIndex + 1) <= buffer)

            // 只有当位置满足，且业务状态允许时，才返回 true
            canTriggerPositionally && state.hasMore && !state.isLoadingMore && !state.isRefreshing
        }
            .distinctUntilChanged() // 只有状态从 false 变为 true 时才触发
            .filter { it }           // 只处理为 true 的情况
            .collect {
                currentOnLoadMore()
            }
    }
}

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