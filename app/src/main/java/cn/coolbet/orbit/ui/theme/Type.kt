package cn.coolbet.orbit.ui.theme

import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.coolbet.orbit.R

val SansFontFamily = FontFamily(
    Font(R.font.dm_sans_regular, FontWeight.Normal),
    Font(R.font.dm_sans_medium, FontWeight.Medium),
    Font(R.font.dm_sans_bold, FontWeight.Bold),
)
val MonoFontFamily = FontFamily(
    Font(R.font.dm_mono_regular, FontWeight.Normal),
    Font(R.font.dm_mono_medium, FontWeight.Medium),
)

@Immutable
data class OrBitTypography(
    val R11B50: TextStyle,
    val R11B25: TextStyle,
    val M11B25: TextStyle,
    val M11White00: TextStyle,

    val R13B50: TextStyle,
    val R13B25: TextStyle,
    val M13: TextStyle,
    val M13B25: TextStyle,

    val R15: TextStyle,
    val R15B50: TextStyle,
    val R15B25: TextStyle,
    val M15: TextStyle,
    val M15B25: TextStyle,
    val M15White00: TextStyle,

    val M17: TextStyle,

    val M28: TextStyle,
)

val AppTypography = OrBitTypography(
    R11B50 = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.W400, lineHeight = 13.sp, color = Black50, fontFamily = SansFontFamily),
    R11B25 = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.W400, lineHeight = 13.sp, color = Black25, fontFamily = SansFontFamily),
    M11B25 = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.W500, lineHeight = 13.sp, color = Black25, fontFamily = SansFontFamily),
    M11White00 = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.W500, lineHeight = 13.sp, color = Color.White, fontFamily = SansFontFamily),

    R13B50 = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.W400, lineHeight = 18.sp, color = Black50, fontFamily = SansFontFamily),
    R13B25 = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.W400, lineHeight = 18.sp, color = Black25, fontFamily = SansFontFamily),
    M13 = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.W500, lineHeight = 18.sp, color = Black95, fontFamily = SansFontFamily),
    M13B25 = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.W500, lineHeight = 18.sp, color = Black25, fontFamily = SansFontFamily),

    R15 = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W400, lineHeight = 20.sp, color = Black95, fontFamily = SansFontFamily),
    R15B50 = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W400, lineHeight = 20.sp, color = Black50, fontFamily = SansFontFamily),
    R15B25 = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W400, lineHeight = 20.sp, color = Black25, fontFamily = SansFontFamily),
    M15 = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W500, lineHeight = 20.sp, color = Black95, fontFamily = SansFontFamily),
    M15B25 = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W500, lineHeight = 20.sp, color = Black25, fontFamily = SansFontFamily),
    M15White00 = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W500, lineHeight = 20.sp, color = Color.White, fontFamily = SansFontFamily),

    M17 = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.W500, lineHeight = 22.sp, color = Black95, fontFamily = SansFontFamily),

    M28 = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.W500, lineHeight = 34.sp, color = Black95, fontFamily = SansFontFamily),
)

val LocalAppTypography = staticCompositionLocalOf {
    AppTypography
}

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
/* Other default text styles to override
titleLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
),
labelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
)
*/
)