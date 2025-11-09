package cn.coolbet.orbit.ui.view.home

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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.ObIconGroup
import cn.coolbet.orbit.ui.kit.ObIconItem
import cn.coolbet.orbit.ui.kit.ObTopAppbar
import cn.coolbet.orbit.ui.view.sync.SyncViewModel

val LocalExpandFolder = compositionLocalOf { { _: Long -> } }
val LocalListIsScrolling = compositionLocalOf { false }

object HomeScreen: Screen {
    private fun readResolve(): Any = HomeScreen

    @Composable
    override fun Content() {
        val viewModel = getScreenModel<HomeScreenModel>()
        val state by viewModel.uiState.collectAsState()
        val syncViewModel: SyncViewModel = hiltViewModel()
        val lazyListState = rememberLazyListState()
        val isScrolling by remember {
            derivedStateOf { lazyListState.isScrollInProgress }
        }
        Scaffold (
            topBar = {
                ObTopAppbar(
                    navigationIcon = {
                        ObIcon(id = R.drawable.lines_3, onClick = { NavigatorBus.push(Route.Profile) })
                    },
                    actions = {
                        ObIconGroup(items = listOf(
                            ObIconItem(iconId = R.drawable.sync),
                            ObIconItem(iconId = R.drawable.add, onClick = { }),
                        ))
                    }
                )
            }
        ) { paddingValues ->
            CompositionLocalProvider(
                LocalOverscrollFactory provides null,
//                LocalListIsScrolling provides isScrolling
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    state = lazyListState,
                ) {
                    item { LabelTile("订阅源") }
                    items(state.folders, key = { it.metaId }) { item ->
                        CompositionLocalProvider(
                            LocalExpandFolder provides viewModel::toggleExpanded,
                        ) {
                            FolderTile(item)
                        }
                    }
                    items(state.feeds, key = { it.metaId }) { item ->
                        FeedTile(item)
                    }
                    if (!state.hasMore) {
                        item {
                            NoMoreIndicator(height = 30.dp)
                        }
                    }
                }
            }
        }
    }

}