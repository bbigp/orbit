package cn.coolbet.orbit.module

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.AppTypography


@Preview(showBackground = true)
@Composable
fun CountBadge() {
    Box(
        modifier = Modifier
            .padding(end = 4.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            text = "999+",
            style = AppTypography.M13B25,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}


