package cn.coolbet.orbit.ui.view.list_detail.setting_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.kit.ObtCardView
import cn.coolbet.orbit.ui.kit.ObtMagazineView
import cn.coolbet.orbit.ui.kit.ObtTextOnlyView
import cn.coolbet.orbit.ui.kit.ObtThreadView
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black95


enum class LDDisplayMode(val value: String) {
    Magazine("Magazine"),
    Card("Card"),
    Thread("Thread"),
    TextOnly("Text-Only");

    override fun toString(): String = value
}

@Composable
fun DisplayModePicker(){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DisplayModeItem(
                selected = true,
                displayMode = LDDisplayMode.Magazine,
                modifier = Modifier.weight(1f)
            )
            DisplayModeItem(displayMode = LDDisplayMode.TextOnly, modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DisplayModeItem(
                selected = false,
                displayMode = LDDisplayMode.Thread,
                modifier = Modifier.weight(1f)
            )
            DisplayModeItem(displayMode = LDDisplayMode.Card, modifier = Modifier.weight(1f))
        }
    }
}


@Composable
private fun DisplayModeItem(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    displayMode: LDDisplayMode = LDDisplayMode.Magazine,
) {
    val defaultModifier = Modifier.background(Color(0xFFECECEC), shape = RoundedCornerShape(14.dp))
        .border(width = 2.dp, color = if (selected) Black95 else Color.Transparent, shape = RoundedCornerShape(14.dp))
        .fillMaxWidth()
    Row(
        modifier = defaultModifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Box(
            modifier = Modifier
                .padding(start = 8.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
            content = {
                when (displayMode) {
                    LDDisplayMode.Magazine -> ObtMagazineView()
                    LDDisplayMode.Thread -> ObtThreadView()
                    LDDisplayMode.Card -> ObtCardView()
                    LDDisplayMode.TextOnly -> ObtTextOnlyView()
                }
            }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            displayMode.value, maxLines = 1, textAlign = TextAlign.Center,
            style = if (selected) AppTypography.M15 else AppTypography.M15B50,
            modifier = Modifier.padding(end = 8.dp).weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDisplayModeItem() {
    Column {
        DisplayModeItem()
        DisplayModePicker()
    }
}