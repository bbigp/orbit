package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Stable
data class PagingConfig(
    val pageSize: Int = 30,
    val triggerRatio: Double = 2.0 / 3.0
)

sealed interface PagingResult {
    data object HasMoreData : PagingResult
    data object NoMoreData : PagingResult
    data object Error : PagingResult
}

internal enum class FooterKind { Loading, End, None }

@Stable
class PagingState(
    initialPage: Int = 1,
    initialHasMoreData: Boolean = true
) {
    var currentPage by mutableIntStateOf(initialPage)
        private set

    var hasMoreData by mutableStateOf(initialHasMoreData)
        private set

    var isLoadingMore by mutableStateOf(false)
        private set

    internal val footerKind: FooterKind
        get() = when {
            isLoadingMore && hasMoreData -> FooterKind.Loading
            !hasMoreData -> FooterKind.End
            else -> FooterKind.None
        }

    fun reset(page: Int = 1, hasMore: Boolean = true) {
        currentPage = page
        hasMoreData = hasMore
        isLoadingMore = false
    }

    suspend fun loadMore(
        loadAction: suspend (nextPage: Int) -> PagingResult
    ) {
        if (!hasMoreData || isLoadingMore) return

        val nextPage = currentPage + 1
        isLoadingMore = true
        val result = try {
            loadAction(nextPage)
        } catch (_: Throwable) {
            PagingResult.Error
        }

        when (result) {
            PagingResult.HasMoreData -> {
                currentPage = nextPage
                hasMoreData = true
            }

            PagingResult.NoMoreData -> {
                currentPage = nextPage
                hasMoreData = false
            }

            PagingResult.Error -> {
                // Keep current page and hasMoreData unchanged for retry.
            }
        }
        isLoadingMore = false
    }
}

@Composable
fun rememberPagingState(
    initialPage: Int = 1,
    initialHasMoreData: Boolean = true
): PagingState {
    return remember(initialPage, initialHasMoreData) {
        PagingState(initialPage = initialPage, initialHasMoreData = initialHasMoreData)
    }
}

@Composable
fun <T> PagingLazyColumn(
    items: List<T>,
    pagingState: PagingState,
    onLoadMore: suspend (nextPage: Int) -> PagingResult,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    pagingConfig: PagingConfig = PagingConfig(),
    key: ((index: Int, item: T) -> Any)? = null,
    loadingFooter: @Composable () -> Unit = { LoadMoreIndicator() },
    endFooter: @Composable () -> Unit = { NoMoreIndicator() },
    content: @Composable LazyItemScope.(item: T) -> Unit
) {
    LaunchedEffect(listState, items.size, pagingState.currentPage, pagingState.hasMoreData, pagingState.isLoadingMore) {
        snapshotFlow {
            shouldTriggerLoadMore(
                listState = listState,
                itemCount = items.size,
                pagingState = pagingState,
                pagingConfig = pagingConfig
            )
        }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                pagingState.loadMore(onLoadMore)
            }
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
            footerKind = pagingState.footerKind,
            showEnd = items.isNotEmpty(),
            loadingFooter = loadingFooter,
            endFooter = endFooter
        )
    }
}

private fun shouldTriggerLoadMore(
    listState: LazyListState,
    itemCount: Int,
    pagingState: PagingState,
    pagingConfig: PagingConfig
): Boolean {
    if (!pagingState.hasMoreData || pagingState.isLoadingMore) return false
    if (itemCount <= 0) return false
    if (pagingState.currentPage <= 0) return false

    val maxVisibleIndex = listState.layoutInfo.visibleItemsInfo.maxOfOrNull { it.index } ?: -1
    if (maxVisibleIndex < 0) return false

    val triggerIndex = pagingConfig.pageSize * (pagingState.currentPage - 1) +
            (pagingConfig.pageSize * pagingConfig.triggerRatio).toInt()

    return maxVisibleIndex >= triggerIndex
}

private fun LazyListScope.pagingFooter(
    footerKind: FooterKind,
    showEnd: Boolean,
    loadingFooter: @Composable () -> Unit,
    endFooter: @Composable () -> Unit
) {
    when (footerKind) {
        FooterKind.Loading -> item(key = "paging_footer_loading") { loadingFooter() }
        FooterKind.End -> if (showEnd) item(key = "paging_footer_end") { endFooter() }
        FooterKind.None -> Unit
    }
}

@Composable
fun PagingLoadingFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(strokeWidth = 2.dp)
    }
}

@Composable
fun PagingEndFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No more items",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

