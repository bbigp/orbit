package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.coolbet.orbit.ui.view.listdetail.ExtendedPullRefreshLayout

@Composable
fun <T> ObLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    pagingState: PagingLoadState,
    onRefresh: (() -> Unit)? = null,
    onLoadMore: (() -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
    onLongPull: (() -> Unit)? = onRefresh,
    key: ((index: Int, item: T) -> Any)? = null,
    content: @Composable LazyItemScope.(item: T) -> Unit
) {
    val listContent: @Composable () -> Unit = {
        PagingLazyColumn(
            modifier = modifier,
            items = items,
            pagingState = pagingState,
            onLoadMore = onLoadMore,
            listState = listState,
            key = key,
            content = content
        )
    }

    if (onRefresh != null) {
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
