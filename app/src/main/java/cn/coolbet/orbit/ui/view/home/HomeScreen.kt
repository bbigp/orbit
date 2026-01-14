package cn.coolbet.orbit.ui.view.home

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.model.domain.User
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.ObIconGroup
import cn.coolbet.orbit.ui.kit.ObTopAppbar
import cn.coolbet.orbit.ui.kit.ProgressIndicator
import cn.coolbet.orbit.ui.view.syncer.SyncViewModel

val LocalExpandFolder = compositionLocalOf { { _: Long -> } }
val LocalListIsScrolling = compositionLocalOf { false }
val LocalUnreadState = compositionLocalOf<State<Map<String, Int>>> { mutableStateOf(emptyMap()) }
val LocalUserState = compositionLocalOf<State<User>> { mutableStateOf(User.EMPTY) }

object HomeScreen: Screen {
    private fun readResolve(): Any = HomeScreen

    @Composable
    override fun Content() {
        val model = getScreenModel<HomeScreenModel>()
        val state by model.uiState.collectAsState()
        val unreadState = model.unreadMapState.collectAsState()
        val userState = model.userState.collectAsState()

        val syncViewModel: SyncViewModel = hiltViewModel()
        val isSyncing by syncViewModel.isSyncing.collectAsStateWithLifecycle()

        val lazyListState = rememberLazyListState()
        val isScrolling by remember {
            derivedStateOf { lazyListState.isScrollInProgress }
        }

        Scaffold (
            topBar = {
                ObTopAppbar(
                    navigationIcon = {
                        ObIcon(
                            id = R.drawable.lines_3,
                            modifier = Modifier.clickable(onClick = { NavigatorBus.push(Route.Profile) }),
                        )
                    },
                    actions = {
                        ObIconGroup {
                            if (isSyncing)
                                ProgressIndicator()
                            else
                                ObIcon(
                                    id = R.drawable.sync,
                                    contentScale = ContentScale.None,
                                    modifier = Modifier.clickable {
                                        syncViewModel.syncData(checkLastExecuteTime = false)
                                    },
                                )
                            ObIcon(id = R.drawable.add, modifier = Modifier.clickable {
                            })
                        }
                    }
                )
            }
        ) { paddingValues ->
            CompositionLocalProvider(
                LocalOverscrollFactory provides null,
                LocalUnreadState provides unreadState,
                LocalUserState provides userState,
//                LocalListIsScrolling provides isScrolling
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    state = lazyListState,
                ) {
                    item { LabelTile("订阅源") }
                    items(state.folders, key = { it.metaId.toString() }) { item ->
                        CompositionLocalProvider(
                            LocalExpandFolder provides model::toggleExpanded,
                        ) {
                            FolderTile(item)
                        }
                    }
                    items(state.feeds, key = { it.metaId.toString() }) { item ->
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