package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black04
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.Black25
import cn.coolbet.orbit.ui.theme.Black95
import cn.coolbet.orbit.ui.theme.ContainerRed
import cn.coolbet.orbit.ui.theme.ContentRed
import cn.coolbet.orbit.ui.theme.OrbitTheme

@Preview(showBackground = true)
@Composable
fun PreviewObIconTextButton() {
    Column {
        ObIconTextButton("Add Now", icon = R.drawable.add, sizes = OButtonDefaults.mediumPadded)
        ObIconTextButton("Add Now", iconOnRight = true, icon = R.drawable.add, sizes = OButtonDefaults.mediumPadded)
    }
}


@Composable
fun ObIconTextButton(
    content: String,
    icon: Int,
    disable: Boolean = false,
    onClick: () -> Unit = {},
    iconOnRight: Boolean = false,
    colors: OButtonColors = OButtonDefaults.buttonColor,
    sizes: OButtonSize = OButtonDefaults.buttonSize,
) {
    val contentColor = if (disable) colors.disabledContentColor else colors.contentColor
    val iconContent = @Composable {
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = icon),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(contentColor),
        )

    }
    val textContent = @Composable {
        Text(
            content, maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = sizes.fontSize.copy(color = contentColor)
        )
    }
    val spacerContent = @Composable {
        Spacer(modifier = Modifier.width(8.dp))
    }
    val orderedContent = when (iconOnRight) {
        true -> listOf(textContent, spacerContent, iconContent)
        false -> listOf(iconContent, spacerContent, textContent)
    }
    OButton(
        content = {
            orderedContent.forEach { composable ->
                composable()
            }
        },
        onClick = onClick, disable = disable, colors = colors, sizes = sizes,
    )
}

@Composable
fun ObTextButton(
    content: String,
    disable: Boolean = false,
    onClick: () -> Unit = {},
    colors: OButtonColors = OButtonDefaults.buttonColor,
    sizes: OButtonSize = OButtonDefaults.buttonSize,
) {
    OButton(
        content = {
            Text(
                content, maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = sizes.fontSize.copy(if (disable) colors.disabledContentColor else colors.contentColor))
        },
        onClick = onClick, disable = disable, colors = colors, sizes = sizes,
    )
}

@Composable
fun OButton(
    onClick: () -> Unit = {},
    disable: Boolean = false,
    colors: OButtonColors = OButtonDefaults.buttonColor,
    sizes: OButtonSize = OButtonDefaults.buttonSize,
    content: @Composable RowScope.() -> Unit,
){
    var modifier = Modifier.height(sizes.height)
        .background(
            if (disable) colors.disabledContainerColor else colors.containerColor,
            shape = RoundedCornerShape(sizes.radius))
        .border(width = 1.dp, color = colors.borderColor, shape = RoundedCornerShape(sizes.radius))
        .clickable(
            enabled = !disable, onClick = onClick, role = Role.Button
        )
    if (sizes.horizontalPadding == 0.dp) {
        modifier = modifier.fillMaxWidth()
    } else{
        modifier = modifier.padding(horizontal = sizes.horizontalPadding).wrapContentWidth()
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

data class OButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
    val borderColor: Color = Color.Transparent,
)
data class OButtonSize(
    val height: Dp,
    val horizontalPadding: Dp = 0.dp,
    val radius: Dp,
    val fontSize: TextStyle,
)

object OButtonDefaults {

    val buttonColor: OButtonColors get() = primary
    val buttonSize: OButtonSize get() = medium
    val primary = OButtonColors( //黑色背景 白色字体
        containerColor = Black95, contentColor = Color.White,
        disabledContainerColor = Black08, disabledContentColor = Black25,
    )
    val secondary = OButtonColors( //灰色背景 黑色字体
        containerColor = Black04, contentColor = Black95,
        disabledContainerColor = Black04, disabledContentColor = Black25,
    )
    val ghost = OButtonColors( //透明背景 黑色字体
        containerColor = Color.Transparent, contentColor = Black95,
        disabledContainerColor = Color.Transparent, disabledContentColor = Black25,
    )
    val stroked = OButtonColors( //透明背景 黑色字体 带描边
        containerColor = Color.Transparent, contentColor = Black95,
        disabledContainerColor = Black04, disabledContentColor = Black25,
        borderColor = Black08,
    )
    val danger = OButtonColors( //红色背景 红色字体
        containerColor = ContainerRed, contentColor = ContentRed,
        disabledContainerColor = Black04, disabledContentColor = Black25,
    )
    val dangerGhost = OButtonColors( //透明背景 红色字体
        containerColor = Color.Transparent, contentColor = ContentRed,
        disabledContainerColor = Color.Transparent, disabledContentColor = Black25,
    )


    val small = OButtonSize(
        height = 36.dp, radius = 8.dp, fontSize = AppTypography.M13B25,
    )
    val smallPadded = OButtonSize(
        height = 36.dp, radius = 8.dp, fontSize = AppTypography.M13B25, horizontalPadding = 16.dp
    )
    val medium = OButtonSize(
        height = 44.dp, radius = 10.dp, fontSize = AppTypography.M15White00,
    )
    val mediumPadded = OButtonSize(
        height = 44.dp, radius = 10.dp, fontSize = AppTypography.M15White00, horizontalPadding = 32.dp
    )
    val large = OButtonSize(
        height = 52.dp, radius = 12.dp, fontSize = AppTypography.M17,
    )
//    val largePadded = ObButtonSize(
//        height = 52.dp, radius = 12.dp, fontSize = AppTypography.M17, horizontalPadding = 16.dp
//    )

}

@Preview(showBackground = true)
@Composable
fun PreviewObButtonSize() {
    OrbitTheme {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            ObTextButton(content = "continue", sizes = OButtonDefaults.small)
            Spacer(modifier = Modifier.height(10.dp))
            ObTextButton(content = "continue", sizes = OButtonDefaults.medium)
            Spacer(modifier = Modifier.height(10.dp))
            ObTextButton(content = "continue", sizes = OButtonDefaults.large)
            Spacer(modifier = Modifier.height(10.dp))
            ObTextButton(content = "continue", sizes = OButtonDefaults.smallPadded)
            Spacer(modifier = Modifier.height(10.dp))
            ObTextButton(content = "continue", sizes = OButtonDefaults.mediumPadded)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewObButtonColor() {
    OrbitTheme {
        Column(
            modifier = Modifier.background(Color.White)
        ) {
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = true, colors = OButtonDefaults.primary)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = false, colors = OButtonDefaults.primary)
                }
            }
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = true, colors = OButtonDefaults.secondary)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = false, colors = OButtonDefaults.secondary)
                }
            }
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = true, colors = OButtonDefaults.ghost)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = false, colors = OButtonDefaults.ghost)
                }
            }
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = true, colors = OButtonDefaults.stroked)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = false, colors = OButtonDefaults.stroked)
                }
            }
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = true, colors = OButtonDefaults.danger)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = false, colors = OButtonDefaults.danger)
                }
            }
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = true, colors = OButtonDefaults.dangerGhost)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    ObTextButton(content = "continue", disable = false, colors = OButtonDefaults.dangerGhost)
                }
            }
        }
    }
}