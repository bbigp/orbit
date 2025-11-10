package cn.coolbet.orbit.tet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import cn.coolbet.orbit.ui.theme.OrbitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            OrbitTheme {
                SystemBarAppearance()
                AppLifecycleTracker(syncAction = {
                    // ⭐️ App 从后台回到前台时执行的同步操作
                    Log.d("Lifecycle", "App is back to foreground! Performing sync.")
                })
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


