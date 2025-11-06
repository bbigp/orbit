package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.OrbitTheme



@Preview(showBackground = true)
@Composable
fun PreviewButton() {
    OrbitTheme {
        Column {
            Button(onClick = {}) { Text("continue") }
            Row {
                Box(modifier = Modifier.weight(1f)){ObAsyncTextButton(content = "continue")}
                Box(modifier = Modifier.weight(1f)){ObAsyncTextButton(content = "continue", isLoading = true)}
            }
        }
    }
}

@Composable
fun ObAsyncTextButton(
    content: String,
    isLoading: Boolean = false,
    onClick: () -> Unit = {},
    colors: OButtonColors = OButtonDefaults.buttonColor,
    sizes: OButtonSize = OButtonDefaults.buttonSize,
) {
    @Composable
    fun ButtonTextContent() {
        Text(
            content, maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = sizes.fontSize.copy(colors.contentColor)
        )
    }

    ObAsyncButton(
        onClick = onClick, colors = colors, sizes = sizes, isLoading = isLoading,
        content = {
            ButtonTextContent()
        },
        onLoadingContent = {
            ProgressIndicator(size = 24.dp, color = colors.contentColor)
            Spacer(modifier = Modifier.size(8.dp))
            ButtonTextContent()
        }
    )
}

@Composable
fun ObAsyncButton(
    onClick: () -> Unit = {},
    isLoading: Boolean = false,
    colors: OButtonColors = OButtonDefaults.buttonColor,
    sizes: OButtonSize = OButtonDefaults.buttonSize,
    content: @Composable RowScope.() -> Unit,
    onLoadingContent: @Composable RowScope.() -> Unit,
) {
    OButton(
        onClick = onClick, colors = colors, sizes = sizes,
        content = if (isLoading) onLoadingContent else content
    )
}