package cn.coolbet.orbit.ui.view.entries

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black04
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.ContainerSecondary
import cn.coolbet.orbit.ui.view.FeedIcon
import cn.coolbet.orbit.ui.view.FeedIconDefaults
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriesSheet(
    meta: Meta,
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
){
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White,
            dragHandle = {
                DragHandle()
            },
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 2.dp, bottom = 21.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            ) {
                ListCard(meta)
                Spacer(modifier = Modifier.height(12.dp))

                Text("View", maxLines = 1, style = AppTypography.M15B25, modifier = Modifier.padding(horizontal = 4.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.height(68.dp))
                Spacer(modifier = Modifier.height(16.dp))

                Text("Sort by", maxLines = 1, style = AppTypography.M15B25, modifier = Modifier.padding(horizontal = 4.dp))
                Spacer(modifier = Modifier.height(6.dp))
                //排序
                Spacer(modifier = Modifier.height(16.dp))


                Spacer(modifier = Modifier.height(8.dp))
                // card unread only Group by Publication Date


            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun PreviewListCard() {
    val meta = Feed.EMPTY.copy(title = "少数派 - sspai", feedURL = "htts://sspai.com/feed")
    ListCard(meta)
}


@Composable
fun ListCard(
    meta: Meta
) {
    Column (
        modifier = Modifier.padding(vertical = 8.dp)
            .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = Black08, shape = RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            FeedIcon(meta.url, meta.title, FeedIconDefaults.LARGE)

            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(meta.title, style = AppTypography.M15, maxLines = 1)
                Text(meta.url, style = AppTypography.R13B50, maxLines = 1)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Box(
                modifier = Modifier.height(48.dp).width(60.dp)
                    .background(Black04, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                )
            }


            Spacer(modifier = Modifier.width(6.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}