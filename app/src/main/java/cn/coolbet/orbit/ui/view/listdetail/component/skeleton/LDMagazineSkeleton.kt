package cn.coolbet.orbit.ui.view.listdetail.component.skeleton

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp


@Composable
fun LDMagazineSkeleton() {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.size(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
            )
            Spacer(modifier = Modifier
                .padding(start = 6.dp)
                .clip(RoundedCornerShape(3.dp))
                .height(12.dp)
                .width(72.dp)
                .shimmer()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.padding(top = 3.dp, bottom = 3.dp)) {
                    Spacer(modifier = Modifier
                        .height(14.dp)
                        .weight(1.5f)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Spacer(modifier = Modifier.padding(vertical = 3.dp)
                    .height(12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(3.dp))
                    .shimmer()
                )
                Row(
                    modifier = Modifier.padding(top = 3.dp, bottom = 3.dp)
                ) {
                    Spacer(modifier = Modifier
                        .height(12.dp)
                        .weight(1.3f)
                        .clip(RoundedCornerShape(3.dp))
                        .shimmer()
                    )
                    Spacer(modifier = Modifier.weight(1.2f))
                }

            }

            Spacer(modifier = Modifier.padding(start = 26.dp)
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmer()
            )

        }
    }
}
