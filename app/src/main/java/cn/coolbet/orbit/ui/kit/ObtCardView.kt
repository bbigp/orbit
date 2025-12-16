package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import cn.coolbet.orbit.ui.theme.Black16

@Composable
fun ObtCardView(
    modifier: Modifier = Modifier
) {
    val defaultModifier = Modifier.background(Color.White, shape = RoundedCornerShape(8.dp))
        .widthIn(72.dp)
    Box(
        modifier = defaultModifier.then(modifier)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
                .wrapContentWidth()
        ) {
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(60.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Black16),
                contentAlignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = 22.5.dp, y = 4.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                IconPhotoMountain(
                    modifier = Modifier
                        .offset(x = 21.dp, y = 7.dp)
                        .size(18.dp, 9.53.dp)
                )
            }



            Spacer(modifier = Modifier.height(4.dp))
            SkeletonShape(
                modifier = Modifier
                    .width(60.dp)
                    .height(5.dp),
                cornerRadius = 2.dp
            )
            Spacer(modifier = Modifier.height(2.dp))
            SkeletonShape(
                modifier = Modifier
                    .width(20.dp)
                    .height(3.dp),
                cornerRadius = 1.5.dp
            )
        }

    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewObtCardView() {
    ObtCardView(modifier = Modifier.width(72.dp))
}