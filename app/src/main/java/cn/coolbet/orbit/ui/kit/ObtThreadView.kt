package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.Black16

@Composable
fun ObtThreadView(
    modifier: Modifier = Modifier
) {
    val defaultModifier = Modifier.background(Color.White, shape = RoundedCornerShape(8.dp))
    Box(
        modifier = defaultModifier.then(modifier)
    ) {
        Row(
            modifier = Modifier.padding(all = 6.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(Black16)
                )
                Spacer(modifier = Modifier.height(2.dp))
                SkeletonShape(
                    modifier = Modifier
                        .width(2.dp)
                        .height(17.dp),
                    cornerRadius = 4.5.dp
                )
            }
            Column(
                modifier = Modifier.padding(start = 4.dp, top = 1.5.dp, bottom = 1.5.dp)
            ) {
                SkeletonShape(
                    modifier = Modifier
                        .width(20.dp)
                        .height(3.dp),
                    cornerRadius = 1.5.dp
                )
                Spacer(modifier = Modifier.height(5.dp))
                SkeletonShape(modifier = Modifier.fillMaxWidth().height(5.dp), cornerRadius = 2.dp)
                Spacer(modifier = Modifier.height(3.dp))
                SkeletonShape(modifier = Modifier.fillMaxWidth().height(5.dp), cornerRadius = 2.dp)
                Spacer(modifier = Modifier.height(3.dp))
                SkeletonShape(modifier = Modifier.width(30.dp).height(5.dp), cornerRadius = 2.dp)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewObtThreadView() {
    ObtThreadView(modifier = Modifier.width(72.dp))
}