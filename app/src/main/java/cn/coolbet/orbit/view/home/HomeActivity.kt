package cn.coolbet.orbit.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.coolbet.orbit.common.ConsumerLong
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.theme.OrbitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OrbitTheme {
                HomePage()
            }
        }
    }
}


val LocalExpandFolder = compositionLocalOf { { _: Long -> } }
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()

    Scaffold { paddingValues ->
        CompositionLocalProvider(LocalOverscrollFactory provides null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
            ) {
                items(state.folders, key = { it.id }) { item ->
                    CompositionLocalProvider(
                        LocalExpandFolder provides viewModel::toggleExpanded,
                    ) {
                        FolderTile(item)
                    }
                }
                items(state.feeds, key = { it.id }) { item ->
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
//        CompositionLocalProvider(LocalOverscrollFactory provides null) {
//            PullToRefreshBox(
//                isRefreshing = state.isLoading,
//                onRefresh = viewModel::load,
//                modifier = Modifier.fillMaxSize()
//                    .padding(paddingValues),
//            ) {
//                LazyColumn (
//                    modifier = Modifier.fillMaxSize(),
//                    state = lazyListState,
//                ) {
//                    items(state.feeds, key = { it.id }) { item ->
//                        FeedTile(item)
//                        HorizontalDivider(
//                            modifier = Modifier.padding(start = 80.dp, end = 16.dp),
//                            thickness = 0.5.dp,
//                            color = Black08
//                        )
//                    }
//
//
//                    if (!state.hasMore) {
//                        item {
//                            NoMoreIndicator()
//                        }
//                    }
//                }
//            }
//        }


