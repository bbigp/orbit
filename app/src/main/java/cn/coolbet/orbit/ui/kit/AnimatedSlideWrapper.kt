package cn.coolbet.orbit.ui.kit

import android.os.Parcelable
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AnimatedSlideWrapper(
    private val initialScreen: @RawValue Screen
) : Screen, Parcelable {

    @Composable
    override fun Content() {
        Navigator(initialScreen) { navigator ->
            SlideTransition(navigator)
        }
    }
}
