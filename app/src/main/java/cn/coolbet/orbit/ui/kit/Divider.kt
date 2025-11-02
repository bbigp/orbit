package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.ObTheme


@Composable
fun SpacerDivider(
    thickness: Dp = 0.5.dp, color: Color = Black08,
    start: Dp = 0.dp, end: Dp = 0.dp,
    background: Color = ObTheme.colors.primaryContainer,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(thickness)
            .background(background)
    ) {
        if (start > 0.dp) {
            Spacer(modifier = Modifier.width(start))
        }
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = thickness, color = color)
        if (end > 0.dp) {
            Spacer(modifier = Modifier.width(end))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSpacerDivider() {
    Box(
        modifier = Modifier.height(20.dp).fillMaxWidth(),
        contentAlignment = Alignment.Center,
        ){
        SpacerDivider(start = 40.dp, end = 40.dp)
    }
}