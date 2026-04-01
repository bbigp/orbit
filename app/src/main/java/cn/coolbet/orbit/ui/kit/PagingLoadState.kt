package cn.coolbet.orbit.ui.kit

interface PagingLoadState {
    val isRefreshing: Boolean
    val isLoadingMore: Boolean
    val hasMore: Boolean
    val appendError: Throwable?
}

data class SimplePagingLoadState(
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val hasMore: Boolean = false,
    override val appendError: Throwable? = null,
) : PagingLoadState
