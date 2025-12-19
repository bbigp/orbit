package cn.coolbet.orbit.ui.view.content.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.Black95
import cn.coolbet.orbit.ui.theme.GreyBlack
import cn.coolbet.orbit.ui.theme.ObTheme
import cn.coolbet.orbit.ui.theme.PrmBlack
import cn.coolbet.orbit.ui.theme.Sepia

@Composable
fun ContentBgColorSetting() {
    var selectedIndex by remember { mutableStateOf(0) }
    val colors = listOf(
        Color.White,
        ObTheme.colors.secondaryContainer,
        Sepia,
        PrmBlack,
        GreyBlack
    )

    Column {
        Text("Background Color",
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 20.dp),
            style = AppTypography.M15B50
        )
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.forEachIndexed { index, color ->
                BgColorView(
                    color = color,
                    selected = index == selectedIndex,
                    modifier = Modifier.weight(1f)
                        .click { selectedIndex = index }
                )
            }
        }
    }
}


@Composable
fun BgColorView(
    modifier: Modifier = Modifier,
    color: Color,
    selected: Boolean = false
) {
    Box(
        modifier = modifier.height(46.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(color, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .border(0.5.dp, Black08, RoundedCornerShape(10.dp))
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(15.dp)
                    .background(Black95, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(6.36.dp),
                    painter = painterResource(id = R.drawable.check),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBgColorView(){
    BgColorView(Modifier, GreyBlack, true)
}


@Preview(showBackground = true)
@Composable
fun PreviewContentBgColorSetting() {
    ContentBgColorSetting()
}