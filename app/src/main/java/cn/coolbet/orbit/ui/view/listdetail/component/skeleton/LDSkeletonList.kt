package cn.coolbet.orbit.ui.view.listdetail.component.skeleton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.kit.SpacerDivider


@Composable
fun LDSkeletonList() {
    LazyColumn {
        item {
            LDHeaderSkeleton()
        }
        items(20) {
            LDMagazineSkeleton()
            Box(modifier = Modifier.padding(horizontal = 16.dp)) { SpacerDivider() }
        }
    }
}