package cn.coolbet.orbit.ui.kit

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator

fun BottomSheetNavigator.showAnimated(screen: Screen) {
    this.show(AnimatedSlideWrapper(screen))
}