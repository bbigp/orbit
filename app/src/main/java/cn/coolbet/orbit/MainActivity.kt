package cn.coolbet.orbit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.manager.Preference
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import cn.coolbet.orbit.ui.theme.OrbitTheme
import cn.coolbet.orbit.ui.view.home.HomeScreen
import cn.coolbet.orbit.ui.view.login.LoginScreen
import cn.coolbet.orbit.ui.view.syncer.SyncViewModel
import cn.coolbet.orbit.ui.view.syncer.Syncer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var preference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()

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

object MainScreenWrapper: Screen {
    private fun readResolve(): Any = MainScreenWrapper

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var isInitialized by rememberSaveable { mutableStateOf(false) }
        val authModel = getScreenModel<AuthScreenModel>()
        val isLoggedIn = authModel.state.collectAsState().value

        LaunchedEffect(isInitialized, isLoggedIn) {
            if (!isInitialized) {
                val targetScreen = when {
                    !isLoggedIn -> LoginScreen
                    else -> HomeScreen
                }
                navigator.replaceAll(targetScreen)
                isInitialized = true
            }
        }
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            LoadMoreIndicator()
//        }
    }
}

class AuthScreenModel @Inject constructor(
    private val preference: Preference
): StateScreenModel<Boolean>(false) {
    init {
        mutableState.update { !preference.userProfile().isEmpty }
    }
}