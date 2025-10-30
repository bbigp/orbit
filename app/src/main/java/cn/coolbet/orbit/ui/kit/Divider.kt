package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.Black08


@Composable
fun SpacerDivider(modifier: Modifier = Modifier, thickness: Dp = 0.5.dp, color: Color = Black08) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSpacerDivider() {
    Box(
        modifier = Modifier.height(20.dp).fillMaxWidth(),
        contentAlignment = Alignment.Center,
        ){
        SpacerDivider(modifier = Modifier.padding(start = 40.dp, end = 40.dp))
    }
}