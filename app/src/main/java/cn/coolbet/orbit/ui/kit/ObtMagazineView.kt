package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.theme.Black16

@Composable
fun SkeletonShape(modifier: Modifier, cornerRadius: Dp, color: Color = Black16) {
    Spacer(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(color)
    )
}

@Composable
fun ObtMagazineView(
    modifier: Modifier = Modifier
) {
    val defaultModifier = Modifier.background(Color.White, shape = RoundedCornerShape(8.dp))
    Box(
        modifier = defaultModifier.then(modifier)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 7.5.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            // --- 1. 顶部小矩形线 ---
            SkeletonShape(
                modifier = Modifier
                    .width(20.dp)
                    .height(3.dp),
                cornerRadius = 1.5.dp
            )


            // --- 2. 主体 (文本区域 + 图标卡片) ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp), // spacing: 4
                verticalAlignment = Alignment.Top
            ) {

                // --- A. 左侧文本骨架 ---
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    SkeletonShape(modifier = Modifier.fillMaxWidth(0.9f).height(5.dp), cornerRadius = 2.dp)
                    SkeletonShape(modifier = Modifier.fillMaxWidth(0.9f).height(5.dp), cornerRadius = 2.dp)
                    SkeletonShape(modifier = Modifier.width(20.dp).height(5.dp), cornerRadius = 2.dp)
                }

                // --- B. 右侧图标卡片 ---
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Black16),
                    contentAlignment = Alignment.TopStart
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = 4.dp, y = 4.dp)
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    IconPhotoMountain(
                        modifier = Modifier
                            .offset(x = 2.5.dp, y = 9.dp)
                            .width(15.dp)
                            .height(7.5.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewObtMagazineView() {
    ObtMagazineView(modifier = Modifier.width(72.dp))
}