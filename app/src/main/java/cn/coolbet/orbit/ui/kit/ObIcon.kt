package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.ObTheme


@Composable
fun ObIcon(
    id: Int,
    contentScale: ContentScale = ContentScale.Fit,
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
            contentScale = contentScale,
            colorFilter = ColorFilter.tint(color),
        )
    }
}

data class ObIconItem(
    val iconId: Int,
    val onClick: () -> Unit = {},
    val color: Color? = null,
    val background: Color? = null
)

@Composable
fun ObIconGroup(
//    items: List<ObIconItem>,
    color: Color = ObTheme.colors.primary,
    background: Color = ObTheme.colors.primaryContainer,
    spacing: Dp = 16.dp,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp).height(28.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
//    {
//        items.forEach { item ->
//            ObIcon(
//                id = item.iconId,
//                onClick = item.onClick,
//                color = item.color ?: color,
//                background = item.background ?: background
//            )
//        }
//    }
}


@Preview(showBackground = true)
@Composable
fun PreviewOrIcon() {
    CompositionLocalProvider {
        Column {
            ObIcon(R.drawable.lines_3)
            Box(modifier = Modifier.size(28.dp).background(Color.Red)) {
                ObIcon(R.drawable.add)
                ObIcon(R.drawable.sync)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOrIconGroup() {
    CompositionLocalProvider {
        Column {
            ObIconGroup {
                ObIcon(R.drawable.add)
                ObIcon(R.drawable.page)
            }
            Box(
                modifier = Modifier.width((4+4+28+28+16).dp)
                    .height(10.dp).background(Color.Blue)
            )
        }
    }
}
