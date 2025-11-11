package cn.coolbet.orbit.ui.view.sync

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.toRelativeTime
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.ContentRed
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import java.util.Date

object SyncScreen: Screen {
    private fun readResolve(): Any = SyncScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun Content() {
        val model = getScreenModel<SyncScreenModel>()
        val state by model.state.collectAsState()
        val listState = rememberLazyListState()
        val pullState = rememberPullToRefreshState()
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .distinctUntilChanged()
                .filter { !state.isLoadingMore }
                .collect {
                    model.nextPage()
                }
        }

        val density = LocalDensity.current
        val thresholdPx = with(density) { 80.dp.toPx() }

        Scaffold(

        ) { paddingValues ->

            LazyColumn(
                state = listState,
                modifier = Modifier.padding(paddingValues)
                    // 关键：应用 pullToRefresh 修饰符到 LazyColumn
                    .pullToRefresh(
                        state = pullState,
                        isRefreshing = state.isRefreshing,
                        onRefresh = { model.loadInitialData() }
                    )
            ) {
                item(key = "refresh-indicator") {
                    // 刷新时，高度保持在阈值处；否则，高度跟随下拉距离。
                    val itemHeightPx = if (state.isRefreshing) {
                        // 刷新中，高度稳定在阈值，但带有回弹动画
                        animateFloatAsState(targetValue = thresholdPx, label = "refreshHeight").value
                    } else {
                        // 未刷新，高度跟随下拉距离
                        pullState.distanceFraction
                    }

                    // 只有当高度大于 0 时才渲染
                    if (itemHeightPx > 0) {
                        RefreshIndicatorItem(
                            state = pullState,
                            isRefreshing = state.isRefreshing,
                            itemHeightPx = itemHeightPx
                        )
                    }
                }
                items(state.items, key = { it.id!! }) {
                    SyncRecordView(it)
                    SpacerDivider()
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshIndicatorItem(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    itemHeightPx: Float, // 这个高度将由 LazyColumn 动态控制
    circleSize: Dp = 40.dp
) {
    val density = LocalDensity.current
    val maxRelativeOffsetDp = 15.dp
    val maxRelativeOffsetPx = with(density) { maxRelativeOffsetDp.toPx() }

    // ------------------- 相对偏移计算（与上个回答相同） --------------------
    val offsetThreshold = 0.5f
    val offsetFraction = ((state.distanceFraction - offsetThreshold) / (1f - offsetThreshold)).coerceIn(0f, 1f)

    val targetOffsetFraction = if (isRefreshing) 1f else offsetFraction

    val animatedRelativeOffsetX by animateFloatAsState(
        targetValue = -targetOffsetFraction * maxRelativeOffsetPx,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing), label = "RelativeXOffset"
    )
    val animatedRelativeOffsetY by animateFloatAsState(
        targetValue = targetOffsetFraction * maxRelativeOffsetPx,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing), label = "RelativeYOffset"
    )
    // ----------------------------------------------------------------------

    // 根容器高度由外部控制，这里确保指示器在 Item 区域的中央
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // 关键：将高度设置为外部传入的动态高度
            .height(with(density) { itemHeightPx.toDp() }),
        contentAlignment = Alignment.Center
    ) {
        // --- 1. 下层圆 (不动) ---
        CircleIndicator(
            color = Color.Gray.copy(alpha = 0.5f),
            size = circleSize,
            modifier = Modifier.align(Alignment.Center)
        )

        // --- 2. 上层圆 (移动和动画) ---
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                // 应用相对偏移
                .offset {
                    IntOffset(x = animatedRelativeOffsetX.toInt(), y = animatedRelativeOffsetY.toInt())
                }
                .size(circleSize),
            contentAlignment = Alignment.Center
        ) {
            CircleIndicator(
                color = MaterialTheme.colorScheme.primary,
                size = circleSize,
            )

            // 刷新时显示加载动画
            if (isRefreshing) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(circleSize * 0.7f)
                )
            }
        }
    }
}

@Composable
private fun CircleIndicator(color: Color, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        drawCircle(color = color, radius = size.toPx() / 2)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SyncRecordView(record: SyncTaskRecord) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("执行时间: ${record.executeTime.toRelativeTime()} (${record.id})", style = AppTypography.R15)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("状态:", style = AppTypography.R15)
            Text(record.status, style = when(record.status) {
                SyncTaskRecord.FAIL -> AppTypography.R15B50.copy(color = ContentRed)
                else -> AppTypography.R15B50
            })
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("时间:", style = AppTypography.R15)
            Text("${record.fromTime.toRelativeTime()} - ${record.toTime.toRelativeTime()}", style = AppTypography.R15B50)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text("数据:", style = AppTypography.R15)
            Text("feed: ${record.feed}  folder: ${record.folder}  entry: ${record.entry}  media: ${record.media}", style = AppTypography.R15B50)
        }
        if (record.errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(record.errorMsg, style = AppTypography.R15B50)
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewSyncRecordView() {
    val record = SyncTaskRecord(id = 10002, fromTime = Date().time, toTime = Date().time)
    Column {
        SyncRecordView(record)
        SpacerDivider()
        SyncRecordView(record.copy(errorMsg = "null point exception", status = SyncTaskRecord.FAIL))
    }
}