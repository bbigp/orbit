package cn.coolbet.orbit.ui.kit

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.OrbitTheme
import kotlinx.coroutines.launch



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
    onClick: suspend () -> Unit = {},
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
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.loading),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(colors.contentColor),
            )
            Spacer(modifier = Modifier.size(8.dp))
            ButtonTextContent()
        }
    )
}

@Composable
fun ObAsyncButton(
    onClick: suspend () -> Unit = {},
    isLoading: Boolean = false,
    colors: OButtonColors = OButtonDefaults.buttonColor,
    sizes: OButtonSize = OButtonDefaults.buttonSize,
    content: @Composable RowScope.() -> Unit,
    onLoadingContent: @Composable RowScope.() -> Unit,
) {
    var isLoading by remember { mutableStateOf(isLoading) }
    val scope = rememberCoroutineScope()
    val handleClick = handleClick@{
        if (isLoading) return@handleClick
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
        Unit
    }

    OButton(
        onClick = handleClick, colors = colors, sizes = sizes,
        content = if (isLoading) onLoadingContent else content
    )
}