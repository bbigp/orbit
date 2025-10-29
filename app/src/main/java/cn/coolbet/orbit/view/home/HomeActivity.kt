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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.ConsumerLong
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.OrIcon
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
val LocalListIsScrolling = compositionLocalOf { false }
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()

    val isScrolling by remember {
        derivedStateOf { lazyListState.isScrollInProgress }
    }

    Scaffold (
        topBar = {
            M3CustomTopBar()
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M3CustomTopBar(
    // ... 你的点击回调
) {
    // 1. 设置 Top Bar 的目标高度 (50.dp)
    val targetHeight = 50.dp

    // 2. 计算一个与目标高度匹配的 WindowInsets
    // 目的：告诉 TopAppBar 忽略默认的系统边距，并将其内容限制在目标高度内。
    val density = LocalDensity.current
    val customInsets = remember(density, targetHeight) {
        // 创建一个简单的 WindowInsets，其高度就是我们设置的 50.dp
        object : androidx.compose.foundation.layout.WindowInsets {
            override fun getTop(density: Density): Int = with(density) { targetHeight.roundToPx() }
            override fun getBottom(density: Density): Int = 0
            override fun getLeft(density: Density, layoutDirection: LayoutDirection): Int = 0
            override fun getRight(density: Density, layoutDirection: LayoutDirection): Int = 0
        }
    }

    TopAppBar( // 在 Material 3 中，TopAppBar 就是 SmallTopAppBar
        // 🌟 解决高度问题：使用 Modifier.height() 强制 TopAppBar 容器高度
        modifier = Modifier.height(targetHeight),

        // 🌟 解决背景色问题：使用 colors 参数
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White, // 你的白色背景
            // 确保图标和按钮颜色正确
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),

        // 🌟 解决内容居中问题：覆盖默认的 WindowInsets
        // 告诉 TopAppBar 它的内容高度受 50.dp 限制，从而帮助内部的 IconButton 居中。
        windowInsets = customInsets,

        title = { /* 留空 */ },

        navigationIcon = {
            // 内部的 IconButton 负责居中
            IconButton(onClick = { /* ... */ }) {
                // 确保 OrIcon 本身是简洁的 Icon/Image (已在你上个回复中修正)
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


