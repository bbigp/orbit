package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.view.listdetail.ExtendedPullRefreshLayout

@Composable
fun <T> PullRefreshPagingLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    pagingState: PagingLoadState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
    enablePullToRefresh: Boolean = true,
    onLongPull: (() -> Unit)? = onRefresh,
    prefetchItemCount: Int = 4,
    key: ((index: Int, item: T) -> Any)? = null,
    loadingFooter: @Composable () -> Unit = { LoadMoreIndicator() },
    endFooter: @Composable () -> Unit = { NoMoreIndicator() },
    errorFooter: @Composable (Throwable, onRetry: () -> Unit) -> Unit = { _, onRetry ->
        PagingErrorFooter(onRetry = onRetry)
    },
    content: @Composable LazyItemScope.(item: T) -> Unit
) {
    val listContent: @Composable () -> Unit = {
        PagingLazyColumn(
            modifier = modifier,
            items = items,
            pagingState = pagingState,
            onLoadMore = onLoadMore,
            listState = listState,
            prefetchItemCount = prefetchItemCount,
            key = key,
            loadingFooter = loadingFooter,
            endFooter = endFooter,
            errorFooter = errorFooter,
            content = content
        )
    }

    if (enablePullToRefresh) {
        ExtendedPullRefreshLayout(
            isRefreshing = pagingState.isRefreshing,
            listState = listState,
            onRefresh = onRefresh,
            onLongPull = onLongPull,
        ) {
            listContent()
        }
    } else {
        listContent()
    }
}
