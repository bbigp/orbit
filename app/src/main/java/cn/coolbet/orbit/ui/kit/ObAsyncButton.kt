package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    disable: Boolean = false,
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
        onClick = onClick, colors = colors, sizes = sizes, isLoading = isLoading, disable = disable,
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
    disable: Boolean = false,
    colors: OButtonColors = OButtonDefaults.buttonColor,
    sizes: OButtonSize = OButtonDefaults.buttonSize,
    content: @Composable RowScope.() -> Unit,
    onLoadingContent: @Composable RowScope.() -> Unit,
) {
    OButton(
        onClick = onClick, disable = disable, colors = colors, sizes = sizes,
        content = if (isLoading) onLoadingContent else content
    )
}


@Composable
fun ObAsyncIconButton(
    icon: Int,
    isLoading: Boolean = false,
    disable: Boolean = false,
    content: String = "",
    onClick: () -> Unit = {},
    colors: OButtonColors = OButtonDefaults.buttonColor,
    sizes: OButtonSize = OButtonDefaults.buttonSize,
    iconOnRight: Boolean = false,
) {
    val iconContent = @Composable {
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = icon),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(if (disable) colors.disabledContentColor else colors.contentColor),
        )
    }

    val textContent = @Composable {
        if (content.isNotEmpty()) {
            Text(content, maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = sizes.fontSize.copy(color = if (disable) colors.disabledContentColor else colors.contentColor))
        }
    }

    val spacer = @Composable { Spacer(modifier = Modifier.width(8.dp)) }

    val orderedContent = when (iconOnRight) {
        true -> listOf(textContent, spacer, iconContent)
        false -> listOf(iconContent, spacer, textContent)
    }

    ObAsyncButton(
        onClick = onClick,
        isLoading = isLoading,
        disable = disable,
        colors = colors,
        sizes = sizes,
        content = {
            if (content.isEmpty()) {
                iconContent()
            } else {
                orderedContent.forEach { it() }
            }
        },
        onLoadingContent = {
            val spinnerContent = @Composable {
                ProgressIndicator(size = 24.dp, color = colors.contentColor)
            }

            if (content.isEmpty()) {
                // icon-only button -> show spinner only
                spinnerContent()
            } else {
                val orderedLoading = when (iconOnRight) {
                    true -> listOf(textContent, spacer, spinnerContent)
                    false -> listOf(spinnerContent, spacer, textContent)
                }
                orderedLoading.forEach { it() }
            }
        }
    )
}