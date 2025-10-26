package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R


@Composable
fun NoMoreIndicator(height: Dp = 60.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(height),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.vector),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            contentScale = ContentScale.Inside,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewNoMoreDataIndicator(){
    NoMoreIndicator()
}
