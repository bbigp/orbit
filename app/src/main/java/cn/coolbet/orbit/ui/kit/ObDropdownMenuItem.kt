package cn.coolbet.orbit.ui.kit

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black95
import cn.coolbet.orbit.ui.theme.ContentRed


@Composable
fun ObDropdownMenuItem(
    text: String = "",
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    colors: ObMenuItemColors = ObMenuDefaults.defaultColors,
){
    Row(
        modifier = Modifier.height(44.dp).width(240.dp)
            .background(colors.background),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(8.dp))

        if (leadingIcon != null) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = leadingIcon),
                contentDescription = "",
                contentScale = ContentScale.None,
                colorFilter = ColorFilter.tint(colors.color),
            )

        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTypography.R15.copy(color = colors.color),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))

        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = trailingIcon),
                contentDescription = "",
                contentScale = ContentScale.None,
                colorFilter = ColorFilter.tint(colors.color),
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewObDropdownMenuItem() {
    Column {
        ObDropdownMenuItem(text = "menu_1")
        SpacerDivider()
        ObDropdownMenuItem(text = "menu_2", leadingIcon = R.drawable.chevron_right)
        SpacerDivider()
        ObDropdownMenuItem(text = "menu_2", trailingIcon = R.drawable.book)
        SpacerDivider()
        ObDropdownMenuItem(
            text = "menu_3",
            leadingIcon = R.drawable.chevron_right,
            trailingIcon = R.drawable.check_o,
            colors = ObMenuDefaults.defaultColors,
        )
        DropdownMenu(
            expanded = true,
            onDismissRequest = {},
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 0.dp,//阴影
        ) {
            ObDropdownMenuItem(text = "menu_1")
        }
    }
}

object ObMenuDefaults {

    val defaultColors: ObMenuItemColors = ObMenuItemColors(color = Black95, background = Color.White)
    val dangerColors: ObMenuItemColors = ObMenuItemColors(color = ContentRed, background = Color.White)
}

data class ObMenuItemColors(
    val color: Color,
    val background: Color,
)