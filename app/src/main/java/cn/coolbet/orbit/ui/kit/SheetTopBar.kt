package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.ui.theme.AppTypography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetTopBar(
    title: String = "",
    onBack: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(top = 2.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
            .height(34.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ObIcon(
            id = R.drawable.arrow_left,
            modifier = Modifier.background(Color.Transparent).click(onClick = onBack)
        )
        Text(
            title,
            maxLines = 1,
            style = AppTypography.M17,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(28.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSheetTopBar(){
    SheetTopBar(title = "Edit Feed")
}