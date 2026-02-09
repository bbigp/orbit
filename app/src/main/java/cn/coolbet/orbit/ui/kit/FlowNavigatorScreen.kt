package cn.coolbet.orbit.ui.kit

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

data class FlowNavigatorScreen(
    private val initialScreen: Screen
) : Screen {

    @Composable
    override fun Content() {
        Navigator(initialScreen) { navigator ->
            SlideTransition(navigator)
        }
    }
}