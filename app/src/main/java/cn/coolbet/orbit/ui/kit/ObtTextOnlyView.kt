package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ObtTextOnlyView(
    modifier: Modifier = Modifier
){
    val defaultModifier = Modifier
        .shadow(
            elevation = 2.dp,
            shape = RoundedCornerShape(8.dp)
        )
        .background(Color.White, shape = RoundedCornerShape(8.dp))
        .widthIn(max = 72.dp)
    Box(
        modifier = defaultModifier.then(modifier)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 7.5.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            SkeletonShape(
                modifier = Modifier
                    .width(20.dp)
                    .height(3.dp),
                cornerRadius = 1.5.dp
            )
            Spacer(modifier = Modifier.height(5.dp))
            SkeletonShape(
                modifier = Modifier.height(5.dp)
                    .fillMaxWidth(1f)
                    .widthIn(max = 60.dp),
                cornerRadius = 2.dp
            )
            Spacer(modifier = Modifier.height(3.dp))
            SkeletonShape(
                modifier = Modifier.height(5.dp)
                    .fillMaxWidth(1f)
                    .widthIn(max = 60.dp),
                cornerRadius = 2.dp
            )
            Spacer(modifier = Modifier.height(3.dp))
            SkeletonShape(modifier = Modifier.height(5.dp).width(30.dp), cornerRadius = 2.dp)
        }
    }

}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewObtTextOnlyView() {
    ObtTextOnlyView(modifier = Modifier.width(72.dp))
}