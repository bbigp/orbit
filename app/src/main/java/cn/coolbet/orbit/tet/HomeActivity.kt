package cn.coolbet.orbit.tet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.navigator.Navigator
import cn.coolbet.orbit.MemoryStore
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import cn.coolbet.orbit.ui.theme.OrbitTheme
import cn.coolbet.orbit.view.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    @Inject lateinit var store: MemoryStore

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        //Compose 渲染前，阻止 Activity 内容显示，直到状态准备好
        splashScreen.setKeepOnScreenCondition {
            !store.isReady.value
        }
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SystemBarAppearance()
            OrbitTheme {
                Navigator(screen = HomeScreen)
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


