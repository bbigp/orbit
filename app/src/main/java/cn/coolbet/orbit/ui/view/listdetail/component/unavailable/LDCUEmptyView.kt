package cn.coolbet.orbit.ui.view.listdetail.component.unavailable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography


/// List Detail Content Unavailable Empty View
@Preview(showBackground = true)
@Composable
fun LDCUEmptyView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.list_graph),
                contentDescription = "",
                contentScale = ContentScale.Fit,
            )
            Text("No content yet", maxLines = 1, style = AppTypography.M15B25)
            Spacer(modifier = Modifier.height(24.dp))

//        ObIconTextButton(
//            "Add Feed Now",
//            icon = R.drawable.add,
//            colors = OButtonDefaults.primary,
//            sizes = OButtonDefaults.mediumPadded
//        )
//        Spacer(modifier = Modifier.height(24.dp))
        }
    }
}