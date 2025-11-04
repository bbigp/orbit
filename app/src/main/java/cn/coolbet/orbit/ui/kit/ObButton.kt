package cn.coolbet.orbit.ui.kit

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black04
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.Black25
import cn.coolbet.orbit.ui.theme.Black95
import cn.coolbet.orbit.ui.theme.ContainerRed
import cn.coolbet.orbit.ui.theme.ContentRed
import cn.coolbet.orbit.ui.theme.OrbitTheme
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun PreviewButton() {
    OrbitTheme {
        Column {
            OButton(content = {}, isLoading = false, enabled = true)
            OButton(content = {}, isLoading = true, enabled = true)
            OButton(content = {}, isLoading = false, enabled = false)

            Button(onClick = {}) { Text("continue") }
            TextButton(onClick = {}, content = {
                Text("continue")
            })
            IconButton(onClick = {}) { Text("continue") }
            OButton(content = {})
        }
    }
}

@Composable
fun ObTextButton(
    content: String,
    onClick: () -> Unit = {},
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: OButtonColors = OButtonDefaults.buttonColor,
    sizes: OButtonSize = OButtonDefaults.buttonSize,
) {
    OButton(
        content = {},
        onClick = onClick, isLoading
    )
}

@Composable
fun ObAsyncButton(
    content: @Composable RowScope.() -> Unit,
    onClick: suspend () -> Unit = {},
    enabled: Boolean = true,
    colors: OButtonColors = OButtonDefaults.buttonColor,
    sizes: OButtonSize = OButtonDefaults.buttonSize,
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val handleClick = {
        scope.launch {
            isLoading = true
            try {
                onClick()
            } catch (e: Exception) {
                Log.e("AsyncOButton", "Async operation failed", e)
            } finally {
                isLoading = false
            }
        }
    }
    OButton(
        content = content, onClick = handleClick, isLoading = isLoading,
        enabled = enabled, colors = colors, sizes = sizes,
    )
}

@Composable
fun OButton(
    content: @Composable RowScope.() -> Unit,
    onClick: () -> Unit = {},
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: OButtonColors = OButtonDefaults.buttonColor,
    sizes: OButtonSize = OButtonDefaults.buttonSize,
){
    var modifier = Modifier.height(sizes.height)
        .background(
            if (enabled && !isLoading) colors.containerColor else colors.disabledContainerColor,
            shape = RoundedCornerShape(sizes.radius))
        .border(width = 1.dp, color = colors.borderColor, shape = RoundedCornerShape(sizes.radius))
    if (sizes.horizontalPadding == 0.dp) {
        modifier = modifier.fillMaxWidth()
    } else{
        modifier = modifier.padding(horizontal = sizes.horizontalPadding).wrapContentWidth()
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("icon", maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = sizes.fontSize.copy(color = if (enabled && !isLoading) colors.contentColor else colors.disabledContentColor)
        )
    }
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
            OButton(content = {}, sizes = OButtonDefaults.small)
            Spacer(modifier = Modifier.height(10.dp))
            OButton(content = {}, sizes = OButtonDefaults.medium)
            Spacer(modifier = Modifier.height(10.dp))
            OButton(content = {}, sizes = OButtonDefaults.large)
            Spacer(modifier = Modifier.height(10.dp))
            OButton(content = {}, sizes = OButtonDefaults.smallPadded)
            Spacer(modifier = Modifier.height(10.dp))
            OButton(content = {}, sizes = OButtonDefaults.mediumPadded)
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
                    OButton(content = {}, enabled = true, colors = OButtonDefaults.primary)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    OButton(content = {}, enabled = false, colors = OButtonDefaults.primary)
                }
            }
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    OButton(content = {}, enabled = true, colors = OButtonDefaults.secondary)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    OButton(content = {}, enabled = false, colors = OButtonDefaults.secondary)
                }
            }
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    OButton(content = {}, enabled = true, colors = OButtonDefaults.ghost)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    OButton(content = {}, enabled = false, colors = OButtonDefaults.ghost)
                }
            }
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    OButton(content = {}, enabled = true, colors = OButtonDefaults.stroked)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    OButton(content = {}, enabled = false, colors = OButtonDefaults.stroked)
                }
            }
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    OButton(content = {}, enabled = true, colors = OButtonDefaults.danger)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    OButton(content = {}, enabled = false, colors = OButtonDefaults.danger)
                }
            }
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    OButton(content = {}, enabled = true, colors = OButtonDefaults.dangerGhost)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    OButton(content = {}, enabled = false, colors = OButtonDefaults.dangerGhost)
                }
            }
        }
    }
}