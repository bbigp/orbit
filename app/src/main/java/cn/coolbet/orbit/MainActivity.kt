package cn.coolbet.orbit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cn.coolbet.orbit.manager.PreferenceManager
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import cn.coolbet.orbit.ui.theme.OrbitTheme
import cn.coolbet.orbit.view.home.HomeScreen
import cn.coolbet.orbit.view.login.LoginScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SystemBarAppearance()
            OrbitTheme {
                val initialScreen: Screen = if (!preferenceManager.userProfile().isEmpty) HomeScreen else LoginScreen
                Navigator(screen = initialScreen)
            }
        }
    }
}



