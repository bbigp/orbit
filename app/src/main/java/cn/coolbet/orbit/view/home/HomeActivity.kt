package cn.coolbet.orbit.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.ConsumerLong
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.OrIcon
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import cn.coolbet.orbit.ui.theme.OrbitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SystemBarAppearance()
            OrbitTheme {
                HomeScreen(toProfile = {})
            }
        }
    }
}


val LocalExpandFolder = compositionLocalOf { { _: Long -> } }
val LocalListIsScrolling = compositionLocalOf { false }
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    toProfile: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()

    val isScrolling by remember {
        derivedStateOf { lazyListState.isScrollInProgress }
    }

    Scaffold (
        topBar = {
            M3CustomTopBar(toProfile = toProfile)
        }
    ) { paddingValues ->
        CompositionLocalProvider(
            LocalOverscrollFactory provides null,
            LocalListIsScrolling provides isScrolling
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


@Preview(showBackground = true)
@Composable
fun PreviewM3CustomTopBar(){
    M3CustomTopBar {  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M3CustomTopBar(
    toProfile: () -> Unit
) {
    TopAppBar(
        // 🌟 关键：使用 heightIn 限制内容区高度，同时允许 TopAppBar 处理状态栏 Insets
        // 这样最终高度 = (状态栏高度) + 50dp，但图标只在 50dp 区域居中
        modifier = Modifier.heightIn(max = 50.dp),

        // 🌟 解决背景色问题：使用 colors 参数
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White, // 你的白色背景
        ),

        // 🌟 关键：让 TopAppBar 自动包含状态栏的高度，这样 Top Bar 就能从屏幕顶部开始画
        windowInsets = TopAppBarDefaults.windowInsets,

        title = { /* 留空 */ },

        navigationIcon = {
            IconButton(onClick = toProfile) {
                OrIcon(R.drawable.lines_3)
            }
        },
        actions = {
            IconButton(onClick = { /* ... */ }) {
                OrIcon(R.drawable.add)
            }
        }
    )
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


