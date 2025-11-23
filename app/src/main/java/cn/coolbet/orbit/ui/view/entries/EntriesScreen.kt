package cn.coolbet.orbit.ui.view.entries

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.ui.kit.InfiniteScrollHandler
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.ObBackTopAppBar
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.view.home.LocalUnreadState

data class EntriesScreen(
    val metaId: MetaId,
): Screen {

    override val key: ScreenKey = metaId.toString()

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.getNavigatorScreenModel<EntriesScreenModel>()
        val unreadState = model.unreadMapState.collectAsState()
        val state by model.state.collectAsState()
        val listState = rememberLazyListState()

        LaunchedEffect(Unit) {
            model.loadInitialData(metaId)
        }

        InfiniteScrollHandler(
            listState = listState,
            stateFlow = model.state,
            onLoadMore = {
                model.nextPage()
            }
        )

        DisposableEffect(Unit) {
            onDispose {
                model.clearState()
            }
        }

        Scaffold(
            topBar = {
                ObBackTopAppBar()
            }
        ) { paddingValues ->
            if (state.isRefreshing) {
                Box(modifier = Modifier.padding(paddingValues)) { EntriesSkeleton() }
            } else {
                CompositionLocalProvider(
                    LocalOverscrollFactory provides null,
                    LocalUnreadState provides unreadState,
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.padding(paddingValues)
                            .fillMaxSize(),
                    ) {
                        item(key = "entry-top-tile") {
                            EntryTopTile(state.extra)
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