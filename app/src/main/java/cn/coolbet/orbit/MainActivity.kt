package cn.coolbet.orbit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import cn.coolbet.orbit.di.AppEntryPoint
import cn.coolbet.orbit.manager.Preference
import cn.coolbet.orbit.ui.kit.ObToast
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import cn.coolbet.orbit.ui.theme.OrbitTheme
import cn.coolbet.orbit.ui.view.home.HomeScreen
import cn.coolbet.orbit.ui.view.login.LoginScreen
import cn.coolbet.orbit.ui.view.syncer.SyncViewModel
import cn.coolbet.orbit.ui.view.syncer.Syncer
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var preference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()

        Log.i("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SystemBarAppearance()
            OrbitTheme {
                val syncViewModel: SyncViewModel = hiltViewModel()
                val initialScreen: Screen = if (!preference.userProfile().isEmpty) HomeScreen else LoginScreen
                val context = LocalContext.current
                val navigatorState = remember {
                    val point = EntryPointAccessors.fromApplication(
                        context.applicationContext,
                        AppEntryPoint::class.java
                    )
                    point.navigatorState()
                }
                Navigator(screen = initialScreen) { navigator ->
                    LaunchedEffect(navigator) {
                        navigatorState.attachNavigator(navigator)
                    }
                    OrbitRouter()
                    Syncer(syncFun = { syncViewModel.syncData() })
                    SlideTransition(navigator)
                    ObToast()
                }

            }
        }
    }
}