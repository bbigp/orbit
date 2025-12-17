package cn.coolbet.orbit.ui.view.list_detail

import android.os.Parcelable
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.ui.kit.InfiniteScrollHandler
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.ObBackTopAppBar
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.ObIconGroup
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.view.list_detail.item.LDMagazine
import cn.coolbet.orbit.ui.view.list_detail.skeleton.LDMagazineSkeleton
import cn.coolbet.orbit.ui.view.list_detail.item.EntryTopTile
import cn.coolbet.orbit.ui.view.list_detail.skeleton.EntryTopTileSkeleton
import cn.coolbet.orbit.ui.view.list_detail.setting_sheet.ListDetailSettingSheet
import cn.coolbet.orbit.ui.view.list_detail.swipable.NoneStateDefinition
import cn.coolbet.orbit.ui.view.list_detail.swipable.ReadStateDefinition
import cn.coolbet.orbit.ui.view.list_detail.swipable.SwipeWrapper
import cn.coolbet.orbit.ui.view.list_detail.swipable.UnreadStateDefinition
import cn.coolbet.orbit.ui.view.entry.QueryContext
import cn.coolbet.orbit.ui.view.home.LocalUnreadState
import cn.coolbet.orbit.ui.view.list_detail.unavailable.LDCUEmptyView
import cn.coolbet.orbit.ui.view.sync.RefreshIndicatorItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListDetailScreen(
    val metaId: MetaId,
): Screen, Parcelable {

    val screenName: String get() = this::class.simpleName ?: ""

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<ListDetailScreenModel, ListDetailScreenModel.Factory> { factory ->
            factory.create(metaId)
        }
        val state by model.state.collectAsState()
        val unreadState = model.unreadMapState.collectAsState()
        val listState = rememberLazyListState()
        val pullState = rememberPullToRefreshState()
        val context = LocalContext.current
        var showBottomSheet by remember { mutableStateOf(false) }

        DisposableEffect(Unit) {
            onDispose { model.onDispose(screenName) }
        }

        InfiniteScrollHandler(
            listState = listState,
            stateFlow = model.state,
            onLoadMore = {
                model.nextPage()
            }
        )

        ListDetailSettingSheet(
            meta = state.meta,
            showBottomSheet = showBottomSheet,
            onDismiss = { showBottomSheet = false }
        )
        Scaffold(
            topBar = {
                ObBackTopAppBar(
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
            CompositionLocalProvider(
                LocalOverscrollFactory provides null,
                LocalUnreadState provides unreadState,
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .pullToRefresh(
                            state = pullState,
                            isRefreshing = state.isRefreshing,
                            onRefresh = {
                                model.loadInitialData()
                            }
                        ),
                ) {
                    item(key = "refresh-indicator") {
                        RefreshIndicatorItem(
                            state = pullState,
                            isRefreshing = state.isRefreshing,
                        )
                    }
                    if (state.isRefreshing) {
                        item {
                            EntryTopTileSkeleton()
                        }
                        items(20) {
                            LDMagazineSkeleton()
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) { SpacerDivider() }
                        }
                    } else {
                        item(key = "entry-top-tile") {
                            EntryTopTile(state.meta)
                        }
                        if (state.items.isEmpty()) {
                            item(key = "no-content-yet") {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LDCUEmptyView()
                                }
                            }
                        } else {
                            items(state.items, key = { it.id }) { item ->
                                SwipeWrapper(
                                    rightSwipeState = if (item.isUnread) ReadStateDefinition.copy(
                                        onClick = { model.toggleReadStatus(item) }
                                    ) else UnreadStateDefinition.copy(
                                        onClick = { model.toggleReadStatus(item) }
                                    ),
                                    leftSwipeState = NoneStateDefinition
                                ) {
                                    LDMagazine(
                                        item,
                                        modifier = Modifier.click {
                                            NavigatorBus.push(
                                                Route.Entry(
                                                    entry = item,
                                                    queryContext = QueryContext.normal
                                                )
                                            )
                                        }
                                    )
                                }
                                SpacerDivider(start = 16.dp, end = 16.dp)
                            }
                            item(key = "indicator") {
                                if (state.hasMore) LoadMoreIndicator() else NoMoreIndicator()
                            }
                        }
                    }
                }
            }
        }
    }

}