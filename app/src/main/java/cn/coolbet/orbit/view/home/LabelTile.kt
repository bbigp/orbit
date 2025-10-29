package cn.coolbet.orbit.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.M13B25
import cn.coolbet.orbit.ui.theme.M17


@Composable
fun LabelTile(title: String) {
    Box(
        modifier = Modifier.height(44.dp)
            .fillMaxWidth()
            .padding(start = 18.dp, end = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(title, style = M17, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLabelTile() {
    LabelTile("订阅源")
}