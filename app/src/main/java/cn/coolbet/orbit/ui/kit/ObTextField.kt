package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black04
import cn.coolbet.orbit.ui.theme.Black25
import cn.coolbet.orbit.ui.theme.Black50
import cn.coolbet.orbit.ui.theme.Black95

@Composable
fun ObIconTextField(
    icon: Int, readOnly: Boolean = false,
    hint: String = "Search", value: String = "",
    background: Color = ObTextFieldDefaults.background,
    sizes: ObTextFieldSize = ObTextFieldDefaults.small,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done,
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onValueChange: (String) -> Unit = {},
    focusRequester: FocusRequester? = null,
) {
    ObTextField(
        hint = hint, value = value, background = background, readOnly = readOnly,
        keyboardOptions = keyboardOptions, keyboardActions = keyboardActions,
        onValueChange = onValueChange, focusRequester = focusRequester,
        leading = {
            Image(
                modifier = Modifier.size(sizes.iconSize),
                painter = painterResource(id = icon),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Black25),
            )
        }
    )
}

@Composable
fun ObTextField(
    modifier: Modifier = Modifier,
    hint: String = "Search", value: String = "",
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit = {},
    background: Color = ObTextFieldDefaults.background,
    sizes: ObTextFieldSize = ObTextFieldDefaults.small,
    leading: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done,
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier.height(sizes.height).fillMaxWidth()
            .clip(RoundedCornerShape(sizes.radius))
            .background(background)
            .padding(sizes.padding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading?.invoke()
        if (leading != null) Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                modifier = modifier.fillMaxWidth()
                    .then(
                        if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier
                    ),
                value = value, onValueChange = onValueChange,
                singleLine = true, readOnly = readOnly,
                textStyle = AppTypography.R15,
                cursorBrush = SolidColor(Black95), // 光标
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
            )
            if (value.isEmpty()) {
                Text(
                    text = hint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = AppTypography.R15B25,
                )
            }
        }

        if (value.isNotEmpty() && !readOnly) {
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                modifier = Modifier.size(sizes.iconSize)
                    .clickable(
                        onClick = {
                            onValueChange("")
                            focusRequester?.requestFocus()
                            keyboardController?.show()
                        }
                    ),
                painter = painterResource(id = R.drawable.x_fill),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Black50),
            )
        }
    }
}

data class ObTextFieldSize(
    val height: Dp,
    val padding: Dp,
    val radius: Dp,
    val iconSize: Dp,
)

object ObTextFieldDefaults {
    val background: Color = Black04
    val small = ObTextFieldSize(
        //设计图padding = 8
        height = 36.dp, padding = 7.dp, radius = 10.dp, iconSize = 20.dp
    )
    val large = ObTextFieldSize(
        height = 52.dp, padding = 12.dp, radius = 12.dp, iconSize = 24.dp
    )

}

@Preview(showBackground = true)
@Composable
fun PreviewObTextField(){
    Column {
        ObIconTextField(R.drawable.search)
        ObTextField(value = "Todo")
        Spacer(modifier = Modifier.height(10.dp))
        ObIconTextField(R.drawable.search, value = "Done", sizes = ObTextFieldDefaults.large)
        ObIconTextField(R.drawable.search, value = "Done", readOnly = true)
    }
}
