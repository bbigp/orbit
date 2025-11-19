package cn.coolbet.orbit.ui.view.folder

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.ObCard


@Composable
fun FoldersCard(
    feeds: List<Feed> = emptyList()
) {
    ObCard(
        contentVertical = 8.dp
    ) {

    }
}


@Composable
fun ObRadio(
    selected: Boolean = false,
    onClick: () -> Unit = {},
    sizes: ObRadioSizes = ObRadioDefaults.medium,
) {
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = Modifier.size(sizes.size)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
                haptics.performHapticFeedback(HapticFeedbackType.ToggleOn)
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier.size(sizes.dotSize),
            painter = painterResource(id = if (selected) R.drawable.selection_radio else R.drawable.selection_off),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
        )
    }
}

object ObRadioDefaults {
    val small: ObRadioSizes = ObRadioSizes(size = 24.dp, dotSize = 15.dp)
    val medium: ObRadioSizes = ObRadioSizes(size = 28.dp, dotSize = 21.dp)
}

data class ObRadioSizes(
    val size: Dp,
    val dotSize: Dp,
)


@Preview(showBackground = true)
@Composable
fun PreviewRadio() {
    Column {
        Row {
            ObRadio(selected = true)
            ObRadio(selected = false)
        }
        Row {
            ObRadio(selected = true, sizes = ObRadioDefaults.small)
        }

        RadioButton(
            selected = true,
            onClick = {}
        )
        RadioButton(
            selected = false,
            onClick = {}
        )
        Checkbox(
            checked = true,
            onCheckedChange = {}
        )
        Checkbox(
            checked = false,
            onCheckedChange = {}
        )
    }
}