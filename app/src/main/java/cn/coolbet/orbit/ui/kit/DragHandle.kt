package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.Black08


@Composable
fun DragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 8.dp, bottom = 0.5.dp),
        contentAlignment = Alignment.Center
    ) {
        Spacer(
            modifier = Modifier
                .size(width = 36.dp, height = 3.5.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Black08)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDragHandle() {
    Column {
        DragHandle()
    }
}