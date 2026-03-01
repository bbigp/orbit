package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Stable
class ListScrollState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val listState: LazyListState,
    val pullState: PullToRefreshState,
    private val flyDistancePx: Float, // 滑动距离
    private val headerIndexOffset: Int,
    val onRefresh: () -> Unit,
    val onLoadMore: () -> Unit
) {
    // 1. 直接提供 Progress 属性，父子组件都能直接读
    val progress: Float by derivedStateOf {
        val firstIndex = listState.firstVisibleItemIndex
        val firstOffset = listState.firstVisibleItemScrollOffset

        when {
            firstIndex <= headerIndexOffset && firstOffset <= 0 -> 0f
            firstIndex > headerIndexOffset -> 1f
            else -> (firstOffset.toFloat() / flyDistancePx).coerceIn(0f, 1f)

            //因为第一项是 RefreshIndicatorItem 所以listState.firstVisibleItemIndex 必须从1开始算
//                    // 明确：如果是第 0 项且位移为 0，进度必须是 0
//                    firstIndex == 0 && firstOffset <= 0 -> 0f
//                    // 如果已经滚过第一项了，进度必须是 1
//                    firstIndex > 0 -> 1f
//                    // 在第一项内部滚动时的计算
//                    else -> (firstOffset.toFloat() / flyDistancePx).coerceIn(0f, 1f)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberListScrollState(
    flyDistance: Dp = 80.dp,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    hasRefreshIndicator: Boolean = true,
): ListScrollState {
    val listState = rememberLazyListState()
    val pullState = rememberPullToRefreshState()
    val density = LocalDensity.current
    val flyDistancePx = with(density) { flyDistance.toPx() }
    val headerIndexOffset = if (hasRefreshIndicator) 1 else 0

    return remember(listState, pullState, flyDistancePx, headerIndexOffset) {
        ListScrollState(listState, pullState, flyDistancePx, headerIndexOffset, onRefresh, onLoadMore)
    }
}