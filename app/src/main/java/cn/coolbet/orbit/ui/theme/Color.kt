package cn.coolbet.orbit.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Black04 = Color(0x0A000000)
val Black08 = Color(0x14000000)
val Black16 = Color(0x29000000)
val Black25 = Color(0x40000000)
val Black50 = Color(0x80000000)
val Black75 = Color(0xBF000000)
val Black95 = Color(0xF2000000)

val ContentRed = Color(0xFFFF3B30)
val ContainerRed = Color(0x1AFF3B30)

val Green = Color(0xFF28CD41)
val Green10 = Color(0x1A28CD41)
val Blue = Color(0xFF007AFF)
val Blue10 = Color(0x1A007AFF)
val Yellow = Color(0xFFFF9500)
val Purple = Color(0xFF5856D6)

val ContainerSecondary = Color(0xD9FFFFFF)//85白

val Skeleton = Color(0xFFEAE8E8)

val Sepia = Color(0xFFFAEFE3)
val PrmBlack = Color(0xFF0D0D0D)
val GreyBlack = Color(0xFF262626)


val DarkColorScheme = darkColorScheme(
    surface = Color.White,
)

val LightColorScheme = lightColorScheme(
    primary = Black95,  //主要颜色  黑色
    onPrimary = Color.White, //主要颜色上的文字图标颜色
    background = Color.White,
    surface = Color.White, //card组件

    //inversePrimary 反向主色。
    //secondary 次色
    //tertiary 第三色
    //surface  组件的“表面”色
    //background 应用的最底层背景色
)

@Immutable
data class ObitColors(
    val primary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val secondaryContainer: Color,
    val tertiary: Color,

)

var LightObitColors = ObitColors(
    primary = Black95,
    primaryContainer = Color.White,
    secondary = Black50,
    secondaryContainer = Color(0xFFF5F5F5),
    tertiary = Black25,
)

val LocalObitColors = staticCompositionLocalOf { LightObitColors }