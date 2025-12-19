package cn.coolbet.orbit.ui.view.content.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.theme.AppTypography


@Composable
fun ContentDisplaySettingsSheetTitle() {
    Box(
        modifier = Modifier
            .padding(top = 2.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
            .height(34.dp)
            .fillMaxWidth()
    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            ObIcon(
//                id = R.drawable.arrow_left,
//                modifier = Modifier.clickable(
//                    onClick = {
//
//                    }
//                )
//            )
//        }
        Text(
            "Display Settings",
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = AppTypography.M17,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewContentDisplaySettingsSheetTitle() {
    ContentDisplaySettingsSheetTitle()
}