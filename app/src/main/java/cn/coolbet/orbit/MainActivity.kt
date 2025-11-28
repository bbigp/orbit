package cn.coolbet.orbit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cn.coolbet.orbit.manager.Preference
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import cn.coolbet.orbit.ui.theme.OrbitTheme
import cn.coolbet.orbit.ui.view.home.HomeScreen
import cn.coolbet.orbit.ui.view.login.LoginScreen
import cn.coolbet.orbit.ui.view.syncer.SyncViewModel
import cn.coolbet.orbit.ui.view.syncer.Syncer
import dagger.hilt.android.AndroidEntryPoint
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
                Navigator(screen = initialScreen) {
                    OrbitRouter()
                    Syncer(syncFun = { syncViewModel.syncData() })
                    CurrentScreen()
                }
            }
        }
    }
}