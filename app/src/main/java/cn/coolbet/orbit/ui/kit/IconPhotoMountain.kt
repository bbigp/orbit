package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.Black95

fun createMountainPath(size: Size): Path {
    val width = size.width
    val height = size.height

    val path = Path()

    // 重新复制您之前定义的路径命令
    path.apply {
        moveTo(x = 0.90921f * width, y = height)
        lineTo(x = 0.09079f * width, y = height)
        cubicTo(x1 = 0.04066f * width, y1 = height, x2 = 0.01305f * width, y2 = 0.88402f * height, x3 = 0.0448f * width, y3 = 0.80677f * height)
        lineTo(x = 0.1863f * width, y = 0.46246f * height)
        cubicTo(x1 = 0.21025f * width, y1 = 0.40419f * height, x2 = 0.25508f * width, y2 = 0.40468f * height, x3 = 0.27871f * width, y3 = 0.46347f * height)
        lineTo(x = 0.32874f * width, y = 0.58796f * height)
        cubicTo(x1 = 0.34055f * width, y1 = 0.61736f * height, x2 = 0.36297f * width, y2 = 0.6176f * height, x3 = 0.37494f * width, y3 = 0.58847f * height)
        lineTo(x = 0.57327f * width, y = 0.10589f * height)
        cubicTo(x1 = 0.597f * width, y1 = 0.04816f * height, x2 = 0.64133f * width, y2 = 0.04801f * height, x3 = 0.66515f * width, y3 = 0.1056f * height)
        lineTo(x = 0.95508f * width, y = 0.80648f * height)
        cubicTo(x1 = 0.98701f * width, y1 = 0.88366f * height, x2 = 0.95943f * width, y2 = height, x3 = 0.90921f * width, y3 = height)
        close()
    }
    return path
}


@Composable
fun IconPhotoMountain(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val mountainPath = createMountainPath(size)

        drawPath(
            path = mountainPath,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIconPhotoMountain(){
    Column(modifier = Modifier.background(Black95)) {
        IconPhotoMountain(modifier = Modifier.size(18.dp, 9.53.dp)) //Card
        IconPhotoMountain(modifier = Modifier.size(15.dp, 7.5.dp)) //Magazine
    }
}