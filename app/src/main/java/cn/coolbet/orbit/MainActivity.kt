package cn.coolbet.orbit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import cn.coolbet.orbit.manager.Preference
import cn.coolbet.orbit.ui.kit.ObToast
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import cn.coolbet.orbit.ui.theme.ObTheme
import cn.coolbet.orbit.ui.theme.OrbitTheme
import cn.coolbet.orbit.ui.view.home.HomeScreen
import cn.coolbet.orbit.ui.view.login.LoginScreen
import cn.coolbet.orbit.ui.view.syncer.SyncViewModel
import cn.coolbet.orbit.ui.view.syncer.Syncer
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    private val preference: Preference by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()

        Log.i("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SystemBarAppearance(dark = isSystemInDarkTheme())
            OrbitTheme {
                val syncViewModel: SyncViewModel = koinViewModel()
                val initialScreen: Screen = if (!preference.userProfile().isEmpty) HomeScreen else LoginScreen

                BottomSheetNavigator(
                    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    sheetBackgroundColor = ObTheme.colors.secondaryContainer,
                ) {
                    Navigator(screen = initialScreen) { navigator ->
                        OrbitRouter()
                        Syncer(syncFun = { syncViewModel.syncData() })
                        SlideTransition(navigator)
                        ObToast()
                    }
                }

            }
        }
    }
}