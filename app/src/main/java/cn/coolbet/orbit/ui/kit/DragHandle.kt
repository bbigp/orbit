package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.Black08


@Composable
fun DragHandle(
    arrow: DragHandleArrow = DragHandleArrow.NONE,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 10.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (arrow == DragHandleArrow.UP) {
                Image(
                    modifier = Modifier.size(width = 40.dp, height = 8.dp),
                    painter = painterResource(id = R.drawable.drag_up),
                    contentDescription = "",
                )
            }
            if (arrow == DragHandleArrow.NONE) {
                Spacer(
                    modifier = Modifier
                        .size(width = 36.dp, height = 3.5.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Black08)
                )
            }
            if (arrow == DragHandleArrow.DOWN) {
                Image(
                    modifier = Modifier.size(width = 40.dp, height = 8.dp),
                    painter = painterResource(id = R.drawable.drag_down),
                    contentDescription = "",
                )
            }
        }
    }
}

enum class DragHandleArrow {
    NONE,
    UP,
    DOWN,
}

@Preview(showBackground = true)
@Composable
fun PreviewDragHandle() {
    Column {
        DragHandle()
        DragHandle(arrow = DragHandleArrow.UP)
        DragHandle(arrow = DragHandleArrow.DOWN)
    }
}
