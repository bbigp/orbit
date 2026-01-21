package cn.coolbet.orbit.ui.view.list_detail.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black50


@Composable
fun LDGroupTitle(
    modifier: Modifier = Modifier,
    date: String
) {
    Column(modifier = Modifier.background(Color.White).fillMaxWidth()) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier.size(16.dp).padding(start = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.padding(top = 4.dp).size(6.dp),
                    painter = painterResource(id = R.drawable.rectangle),
                    contentDescription = "",
                    contentScale = ContentScale.None,
                    colorFilter = ColorFilter.tint(Black50),
                )
            }
            Text(date, maxLines = 1, style = AppTypography.M13B50)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLDGroupTitle(){
    LDGroupTitle(date = "Yesterday")
}
