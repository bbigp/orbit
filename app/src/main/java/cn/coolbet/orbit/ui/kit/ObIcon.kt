package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.ObTheme


@Composable
fun ObIcon(
    id: Int,
    color: Color = ObTheme.colors.primary,
    background: Color = ObTheme.colors.primaryContainer,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.size(28.dp)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = id),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(color),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOrIcon() {
    Column {
        ObIcon(R.drawable.lines_3)
        Box(modifier = Modifier.size(28.dp).background(Color.Red))
    }
}
