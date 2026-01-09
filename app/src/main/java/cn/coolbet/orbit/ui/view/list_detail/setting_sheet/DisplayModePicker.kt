package cn.coolbet.orbit.ui.view.list_detail.setting_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.entity.DisplayMode
import cn.coolbet.orbit.ui.kit.ObtCardView
import cn.coolbet.orbit.ui.kit.ObtMagazineView
import cn.coolbet.orbit.ui.kit.ObtTextOnlyView
import cn.coolbet.orbit.ui.kit.ObtThreadView
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black95
import cn.coolbet.orbit.ui.view.list_detail.LocalChangeDisplayMode


@Composable
fun DisplayModePicker(
    metaId: MetaId,
    displayMode: DisplayMode = DisplayMode.Magazine
){
    val changeDisplayMode = LocalChangeDisplayMode.current
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DisplayModeItem(
                selected = DisplayMode.Magazine == displayMode,
                displayMode = DisplayMode.Magazine,
                modifier = Modifier.weight(1f)
                    .click {
                        changeDisplayMode(metaId, DisplayMode.Magazine)
                    }
            )
            DisplayModeItem(
                selected = DisplayMode.TextOnly == displayMode,
                displayMode = DisplayMode.TextOnly,
                modifier = Modifier.weight(1f)
                    .click {
                        changeDisplayMode(metaId, DisplayMode.TextOnly)
                    }
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DisplayModeItem(
                selected = DisplayMode.Thread == displayMode,
                displayMode = DisplayMode.Thread,
                modifier = Modifier.weight(1f)
                    .click {
                        changeDisplayMode(metaId, DisplayMode.Thread)
                    }
            )
            DisplayModeItem(
                selected = DisplayMode.Card == displayMode,
                displayMode = DisplayMode.Card,
                modifier = Modifier.weight(1f)
//                    .click {
//                        changeDisplayMode(metaId, DisplayMode.Card)
//                    }
            )
        }
    }
}


@Composable
private fun DisplayModeItem(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    displayMode: DisplayMode = DisplayMode.Magazine,
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
                    DisplayMode.Magazine -> ObtMagazineView()
                    DisplayMode.Thread -> ObtThreadView()
                    DisplayMode.Card -> ObtCardView()
                    DisplayMode.TextOnly -> ObtTextOnlyView()
                }
            }
        )
        Text(
            when(displayMode.value) {
                DisplayMode.Magazine.value -> "精选"
                DisplayMode.Card.value -> "卡片"
                DisplayMode.TextOnly.value -> "纯文本"
                DisplayMode.Thread.value -> "动态"
                else -> "精选"
            },
            maxLines = 1, textAlign = TextAlign.Center,
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
        DisplayModePicker(metaId = MetaId("", 0))
    }
}