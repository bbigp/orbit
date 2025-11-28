package cn.coolbet.orbit.ui.view.entries

import android.os.Parcelable
import androidx.compose.foundation.LocalOverscrollFactory
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.ui.kit.InfiniteScrollHandler
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.ObBackTopAppBar
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.view.home.LocalUnreadState
import cn.coolbet.orbit.ui.view.sync.RefreshIndicatorItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class EntriesScreen(
    val metaId: MetaId,
): Screen, Parcelable {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<EntriesScreenModel, EntriesScreenModel.Factory> { factory ->
            factory.create(metaId)
        }
        val state by model.state.collectAsState()
        val unreadState = model.unreadMapState.collectAsState()
        val listState = rememberLazyListState()
        val pullState = rememberPullToRefreshState()

        InfiniteScrollHandler(
            listState = listState,
            stateFlow = model.state,
            onLoadMore = {
                model.nextPage()
            }
        )

        Scaffold(
            topBar = {
                ObBackTopAppBar(
                    actions = {
                        ObIcon(
                            R.drawable.search,
                            onClick = { NavigatorBus.push(Route.SearchEntries(state.meta)) }
                        )
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
                    modifier = Modifier.padding(paddingValues)
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
                            EntryTileSkeleton()
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) { SpacerDivider() }
                        }
                    } else {
                        item(key = "entry-top-tile") {
                            EntryTopTile(state.meta)
                        }
                        items(state.items, key = { it.id }) { item ->
                            EntryTile(item)
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