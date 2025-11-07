package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.ObTheme


@Composable
fun ObCard(
    radius: Dp = 16.dp, horizontal: Dp = 0.dp, vertical: Dp = 0.dp,
    contentHorizontal: Dp = 0.dp, contentVertical: Dp = 4.dp,
    background: Color = ObTheme.colors.primaryContainer,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = horizontal, vertical = vertical)
            .clip(RoundedCornerShape(radius)),
        content = {
            Column(
                modifier = Modifier.padding(vertical = contentVertical, horizontal = contentHorizontal),
                content = content
            )
        },
        colors = CardDefaults.cardColors(contentColor = background)
    )
}

@Preview
@Composable
fun PreviewCard() {
    ObCard {
        ListTileSwitch(
            title = "未读标记", icon = R.drawable.unread_dashed,
            checked = true
        )
        SpacerDivider(start = 52.dp, end = 12.dp)
        ListTileChevronUpDown(
            title = "根文件夹", icon = R.drawable.folder_1,
            trailing = "None"
        )
    }
}