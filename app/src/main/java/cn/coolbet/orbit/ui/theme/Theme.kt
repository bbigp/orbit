package cn.coolbet.orbit.ui.theme

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun OrbitTheme(
    darkTheme: Boolean = false, //isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val obitColors = LightObitColors
    Log.d("ThemeCheck", if (darkTheme) "Using Dark" else "Using Light")
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        CompositionLocalProvider(
            LocalAppTypography provides AppTypography,
            LocalObitColors provides obitColors,
        ) {
            content()
        }
    }
}

object ObTheme {
    val colors: ObitColors
        @Composable
        @ReadOnlyComposable
        get() = LocalObitColors.current
}
