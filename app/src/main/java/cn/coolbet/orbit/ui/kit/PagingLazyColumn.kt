package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private sealed interface FooterState {
    data object Loading : FooterState
    data class Error(val throwable: Throwable) : FooterState
    data object End : FooterState
    data object None : FooterState
}

private fun PagingLoadState.footerState(enableLoadMore: Boolean): FooterState {
    if (!enableLoadMore) return FooterState.End
    return when {
        isLoadingMore -> FooterState.Loading
        appendError != null -> FooterState.Error(requireNotNull(appendError))
        !hasMore -> FooterState.End
        else -> FooterState.None
    }
}

@Composable
fun <T> PagingLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    pagingState: PagingLoadState,
    onLoadMore: (() -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
    prefetchItemCount: Int = 4,
    key: ((index: Int, item: T) -> Any)? = null,
    loadingFooter: @Composable () -> Unit = { LoadMoreIndicator() },
    endFooter: @Composable () -> Unit = { NoMoreIndicator() },
    errorFooter: @Composable (Throwable, onRetry: () -> Unit) -> Unit = { _, onRetry ->
        PagingErrorFooter(onRetry = onRetry)
    },
    content: @Composable LazyItemScope.(item: T) -> Unit
) {
    val canLoadMore = onLoadMore != null

    if (canLoadMore) {
        LoadMoreTrigger(
            listState = listState,
            itemCount = items.size,
            pagingState = pagingState,
            prefetchItemCount = prefetchItemCount,
            onLoadMore = onLoadMore
        )
    }

    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        if (key != null) {
            itemsIndexed(
                items = items,
                key = { index, item -> key(index, item) }
            ) { _, item ->
                content(item)
            }
        } else {
            itemsIndexed(items = items) { _, item ->
                content(item)
            }
        }
        pagingFooter(
            footerState = pagingState.footerState(canLoadMore),
            loadingFooter = loadingFooter,
            endFooter = endFooter,
            errorFooter = errorFooter,
            onRetry = { onLoadMore?.invoke() }
        )
    }
}

private fun LazyListScope.pagingFooter(
    footerState: FooterState,
    loadingFooter: @Composable () -> Unit,
    endFooter: @Composable () -> Unit,
    errorFooter: @Composable (Throwable, onRetry: () -> Unit) -> Unit,
    onRetry: () -> Unit
) {
    when (footerState) {
        FooterState.Loading -> item(key = "paging_footer_loading") { loadingFooter() }
        is FooterState.Error -> item(key = "paging_footer_error") { errorFooter(footerState.throwable, onRetry) }
        FooterState.End -> item(key = "paging_footer_end") { endFooter() }
        FooterState.None -> Unit
    }
}

@Composable
fun PagingErrorFooter(
    text: String = "Load failed, tap to retry",
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onRetry),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}
