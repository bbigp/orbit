package cn.coolbet.orbit.ui.view.content.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black04
import cn.coolbet.orbit.ui.theme.Black25
import cn.coolbet.orbit.ui.theme.Black95
import cn.coolbet.orbit.ui.theme.MonoFontFamily
import cn.coolbet.orbit.ui.theme.SansFontFamily


@Composable
fun ContentFontSetting() {
    var selectedIndex by remember { mutableStateOf(0) }
    val fonts = listOf(
        FontDesign("Aa", "Sans Serif", SansFontFamily),
        FontDesign("Ss", "Serif", SansFontFamily),
        FontDesign("00", "Mono", MonoFontFamily)
    )
    Column {
        Text("Font Style",
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 20.dp),
            style = AppTypography.M15B50
        )
        Row(
            modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fonts.forEachIndexed { index, design ->
                FontDesignCard(
                    modifier = Modifier.weight(1f)
                        .click { selectedIndex = index },
                    selected = index == selectedIndex,
                    fontDesign = design
                )
            }
        }
    }
}

@Composable
fun FontDesignCard(
    modifier: Modifier,
    selected: Boolean = false,
    fontDesign: FontDesign
) {
    Box(
        modifier = modifier.height(80.dp).widthIn(min = 120.dp, max = 200.dp)
            .background(Black04, RoundedCornerShape(16.dp))
            .border(2.dp, if (selected) Black95 else Color.Transparent, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                fontDesign.label,
                maxLines = 1,
                style = AppTypography.M17
                    .copy(
                        color = if (selected) Black95 else Black25,
                        fontFamily = fontDesign.family
                    )
            )
            Text(
                fontDesign.rawValue,
                maxLines = 1,
                style = AppTypography.M15.copy(
                    color = if (selected) Black95 else Black25,
                    fontFamily = fontDesign.family
                )
            )
        }
    }
}

data class FontDesign(
    val label: String,
    val rawValue: String,
    val family: FontFamily
)

@Preview(showBackground = true)
@Composable
fun PreviewContentFontSetting() {
    ContentFontSetting()
}

@Preview(showBackground = true)
@Composable
fun PreviewFontDesignCard() {
    FontDesignCard(
        modifier = Modifier,
        selected = true,
        fontDesign = FontDesign("Aa", "Sans Serif", SansFontFamily)
    )
}