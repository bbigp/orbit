package cn.coolbet.orbit.ui.view.entries

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getNavigatorScreenModel
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.ui.kit.CupertinoActivityIndicator
import cn.coolbet.orbit.ui.kit.InfiniteScrollHandler
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.SpacerDivider

data class EntriesScreen(
    val metaId: MetaId,
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = navigator.getNavigatorScreenModel<EntriesScreenModel, EntriesScreenModel.Factory> { factory ->
            factory.create(metaId)
        }
        val state by model.state.collectAsState()
        val listState = rememberLazyListState()

        InfiniteScrollHandler(
            listState = listState,
            state = state,
            onLoadMore = {
                model.nextPage()
            }
        )

        Scaffold(

        ) { paddingValues ->
            if (state.isRefreshing) {
                //todo: 骨架屏
            } else {
                CompositionLocalProvider(
                    LocalOverscrollFactory provides null,
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.padding(paddingValues)
                            .fillMaxSize(),
                    ) {
                        item(key = "entry-top-tile") {
                            EntryTopTile(Feed.EMPTY)
                        }
                        items(state.items, key = { it.id }) { item ->
                            EntryTile(item)
                            SpacerDivider(start = 16.dp, end = 16.dp)
                        }
                        item(key = "indicator") {
                            if (state.hasMore) {
                                CupertinoActivityIndicator()
                            } else {
                                NoMoreIndicator()
                            }
                        }
                    }
                }
            }
        }
    }


}