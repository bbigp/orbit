package cn.coolbet.orbit.view

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import cn.coolbet.orbit.ui.theme.AppTypography


@Preview(showBackground = true)
@Composable
fun CountBadge() {
    Box(
        modifier = Modifier,
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


