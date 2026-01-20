package cn.coolbet.orbit.ui.view.list_detail

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.common.toBadgeText
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.LoadingState
import cn.coolbet.orbit.manager.asUnreadMarkState
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.domain.UnreadMark
import cn.coolbet.orbit.ui.kit.InfiniteScrollHandler
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.ObIconGroup
import cn.coolbet.orbit.ui.kit.ObTopAppbar
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.view.list_detail.setting_sheet.ListDetailSettingSheet
import cn.coolbet.orbit.ui.view.home.LocalUnreadState
import cn.coolbet.orbit.ui.view.list_detail.unavailable.LDCUEmptyView
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListDetailScreen(
    val metaId: MetaId,
): Screen, Parcelable {

    @SuppressLint("FrequentlyChangingValue")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<ListDetailScreenModel, ListDetailScreenModel.Factory> { factory ->
            factory.create(metaId)
        }
        val state by model.coordinator.state.collectAsState()
        val unreadState = model.unreadMapState.collectAsState()
        val unreadCountMap by unreadState
        val unreadMark by Env.settings.unreadMark.asUnreadMarkState()
        val listState = rememberLazyListState()
        var showBottomSheet by remember { mutableStateOf(false) }
        val density = LocalDensity.current
        val flyDistancePx = with(density) { 80.dp.toPx() }
        val progress by remember {
            derivedStateOf {
                val firstIndex = listState.firstVisibleItemIndex
                val firstOffset = listState.firstVisibleItemScrollOffset

                when {
                    firstIndex <= 1 && firstOffset <= 0 -> 0f
                    firstIndex > 1 -> 1f
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
        val navigator = LocalNavigator.current
        val actions = remember(model) {
            object : ListDetailActions {
                override fun onRefresh() = model.refresh()
                override fun loadMore() = model.nextPage()
                override fun toggleRead(entry: Entry) = model.toggleReadStatus(entry)
                override fun onBack() {
                    model.coordinator.clear()
                    navigator?.pop()
                }
            }
        }

        BackHandler(onBack = { actions.onBack() })

        LaunchedEffect(Unit) {
            model.coordinator.unfreeze()
        }

        InfiniteScrollHandler(
            listState = listState,
            stateFlow = model.coordinator.state,
            onLoadMore = {
                model.nextPage()
            }
        )

        CompositionLocalProvider(
            LocalChangeLDSettings provides model::changeLDSettings,
        ) {
            ListDetailSettingSheet(
                meta = state.meta,
                settings = state.settings,
                showBottomSheet = showBottomSheet,
                onDismiss = { showBottomSheet = false }
            )
        }

        Scaffold(
            topBar = {
                ObTopAppbar(
                    navigationIcon = {
                        ObIcon(
                            id = R.drawable.arrow_left,
                            modifier = Modifier.clickable(onClick = { actions.onBack() })
                        )
                    },
                    title = {
                        Row(
                            modifier = Modifier.graphicsLayer {
                                alpha = progress
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                state.meta.title,
                                maxLines = 1,
                                style = AppTypography.M17
                            )
                            if (unreadMark == UnreadMark.NUMBER) {
                                Box(
                                    modifier = Modifier.padding(start = 8.dp)
                                        .background(Black08, shape = RoundedCornerShape(20.dp))
                                ) {
                                    Text(
                                        (unreadCountMap[metaId.toString()] ?: 0).toBadgeText,
                                        maxLines = 1,
                                        style = AppTypography.M13B25,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        ObIconGroup {
                            ObIcon(
                                R.drawable.search,
                                modifier = Modifier.clickable {
                                    NavigatorBus.push(Route.SearchEntries(state.meta))
                                },
                            )
                            ObIcon(
                                R.drawable.more,
                                modifier = Modifier.clickable {
                                    showBottomSheet = true
                                },
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
                    .fillMaxSize()
            ) {
                when(state.state) {
                    is LoadingState.Loading -> { LDSkeletonList() }
                    is LoadingState.Empty -> { LDCUEmptyView() }
                    else -> {
                        CompositionLocalProvider(
                            LocalOverscrollFactory provides null,
                            LocalListDetailActions provides actions,
                            LocalUnreadState provides unreadState,
                        ) {
                            LDItemList(state, listState, progress)
                        }
                    }
                }
            }
        }
    }

}