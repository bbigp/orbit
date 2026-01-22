package cn.coolbet.orbit.ui.view.sync

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.coolbet.orbit.common.toRelativeTime
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black95
import cn.coolbet.orbit.ui.theme.ContentRed
import java.util.Date

object SyncScreen: Screen {
    private fun readResolve(): Any = SyncScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun Content() {
        val model = koinScreenModel<SyncScreenModel>()
        val state by model.state.collectAsState()

        val listState = rememberLazyListState()
        val pullState = rememberPullToRefreshState()

        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo }
                .collect { layoutInfo ->
                    if (!state.hasMore || state.isLoadingMore || state.isRefreshing || state.items.isEmpty()) return@collect

                    val totalItemsCount = layoutInfo.totalItemsCount
                    if (totalItemsCount == 0) return@collect

                    // 获取最后一个完全可见/部分可见的项目索引
                    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
                    if (lastVisibleItemIndex == null) return@collect

                    val itemsRemaining = totalItemsCount - (lastVisibleItemIndex + 1) // 计算距离底部的剩余项目数
                    if (itemsRemaining <= 5) {
                        model.nextPage()
                    }
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
                        pullState.distanceFraction * thresholdPx
                    }

                    // 只有当高度大于 0 时才渲染
                    if (itemHeightPx > 0) {
                        RefreshIndicatorItem(
                            state = pullState,
                            isRefreshing = state.isRefreshing,
//                            itemHeightPx = itemHeightPx
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
    state: PullToRefreshState,     // 🌟 由 Modifier.pullToRefresh 提供的状态，包含下拉距离信息。
    isRefreshing: Boolean,         // 🌟 是否处于刷新状态 (数据正在加载)。
) {
    // 追踪是否已经触发过震动（防止一次下拉多次震动）
    val vibratedPastThreshold = remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(state.distanceFraction) {
        if (state.isAnimating) {
            return@LaunchedEffect
        }
//        Log.i("RefreshIndicatorItem", "22 ${state.distanceFraction} ${vibratedPastThreshold.value}")
        // 如果达到或超过阈值 (1.0f)，且本轮尚未震动
        if (state.distanceFraction >= 1.0f && !vibratedPastThreshold.value) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            vibratedPastThreshold.value = true
        }

        // 如果距离小于阈值，重置震动状态
        if (state.distanceFraction < 1.0f && vibratedPastThreshold.value) {
            vibratedPastThreshold.value = false
        }
    }
    val density = LocalDensity.current
    val thresholdDp = 40.dp
    val itemHeight = if (isRefreshing) {
        // 🌟 刷新触发时：页面回弹，平滑收缩到 0.dp (250ms 动画)
        animateDpAsState(
            targetValue = 0.dp,
            animationSpec = tween(durationMillis = 80),
            label = "ShrinkHeight"
        ).value
    } else {
        // 🌟 主动下拉时：直接使用原始高度，确保 1:1 跟手
        thresholdDp * state.distanceFraction
    }
//    Log.i("RefreshIndicatorItem", "distanceFraction ${state.distanceFraction} $itemHeight")

    // --- 动画偏移量计算 ---

    // 偏移量的起始触发阈值。当 distanceFraction 达到 0.2f 后，动画才开始启动。
    val offsetThreshold = 0.2f

    // state.distanceFraction 范围通常是 0.0f 到 1.0f
    // 0.0f 的含义: 表示用户没有下拉，或者下拉距离在阈值以下
    // 1.0f 的含义: 表示用户已经下拉到了触发刷新操作的阈值（即，如果此时释放，就会触发 onRefresh）。
    // 将[0.2f, 1f] 转换成 [0f, 1f] 动画参数需要 0到1
    val offsetFraction = ((state.distanceFraction - offsetThreshold) / (1f - offsetThreshold)).coerceIn(0f, 1f)

    // --- 动画定义 ---

    // 上层圆移动的距离
    val maxRelativeOffsetDp = 15.dp
    // 上层圆移动的距离 (15.dp) 转换为像素值，用于动画
    val maxRelativeOffsetPx = with(density) { maxRelativeOffsetDp.toPx() }

    // 上层圆的 X 轴相对偏移量动画。
    // 当 offsetFraction 从 0f 增加到 1f 时，X 轴偏移量从 0 动画到 -maxRelativeOffsetPx。
    val animatedRelativeOffsetX by animateFloatAsState(
        targetValue = -offsetFraction * maxRelativeOffsetPx,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing), label = "RelativeXOffset"
    )
    // 上层圆的 Y 轴相对偏移量动画。
    // 当 offsetFraction 从 0f 增加到 1f 时，Y 轴偏移量从 0 动画到 +maxRelativeOffsetPx。
    val animatedRelativeOffsetY by animateFloatAsState(
        targetValue = offsetFraction * maxRelativeOffsetPx,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing), label = "RelativeYOffset"
    )

    val contentAlpha = if (isRefreshing) {
        0f
    } else {
        // 使用 distanceFraction 本身作为 alpha 值，但放大以确保在距离很小时也能完全显示
        (state.distanceFraction * 5f).coerceIn(0f, 1f)
    }

    // --- 布局容器 ---
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight),
        contentAlignment = Alignment.Center
    ) {
        if (contentAlpha > 0f) {
            Box(
                modifier = Modifier.fillMaxSize().graphicsLayer(alpha = contentAlpha),
                contentAlignment = Alignment.Center
            ) {
                // --- 1. 下层圆 (不动)
                CircleIndicator(
                    color = Black95,
                    size = 24.dp,
                    modifier = Modifier.align(Alignment.Center)
                )

                // --- 2. 上层圆 (移动和动画) ---
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        // 🌟 应用相对偏移：将动画后的像素值转换为 IntOffset
                        .offset {
                            IntOffset(
                                x = animatedRelativeOffsetX.toInt(),
                                y = animatedRelativeOffsetY.toInt()
                            )
                        }
                        .size(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircleIndicator(
                        color = Black95,
                        size = 14.dp
                    )
                }
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