package cn.coolbet.orbit.tet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SystemBarAppearance()
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


