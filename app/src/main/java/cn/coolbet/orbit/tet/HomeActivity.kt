package cn.coolbet.orbit.tet

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cn.coolbet.orbit.dao.UserMapper.Companion.PREFS_NAME
import cn.coolbet.orbit.dao.UserMapper.Companion.USER_KEY
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import cn.coolbet.orbit.ui.theme.OrbitTheme
import cn.coolbet.orbit.view.home.HomeScreen
import cn.coolbet.orbit.view.login.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private val sharedPrefs by lazy {
        this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        val userName = sharedPrefs.getString(USER_KEY, "")
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SystemBarAppearance()
            OrbitTheme {
                val initialScreen: Screen = if (userName != "") HomeScreen else LoginScreen
                Navigator(screen = initialScreen)
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


