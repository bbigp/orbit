package cn.coolbet.orbit.ui.view.list_detail.setting_sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObIconTextButton
import cn.coolbet.orbit.ui.kit.ObTextButton
import cn.coolbet.orbit.ui.kit.ObTextField
import cn.coolbet.orbit.ui.kit.ObTextFieldDefaults
import cn.coolbet.orbit.ui.kit.SheetTopBar

@Composable
fun FeedSettingSheetContent(
    feed: Feed,
    onBack: () -> Unit,
) {
    Column {
        SheetTopBar(title = "Edit Feed", onBack = onBack)
//        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            ObTextField(
                sizes = ObTextFieldDefaults.large,
                value = feed.feedURL,
                readOnly = true,
            )
        }
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
            ObTextField(
                sizes = ObTextFieldDefaults.large,
                value = feed.title
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        //选择框
        Spacer(modifier = Modifier.height(24.dp))
        ObTextButton("Done", sizes = OButtonDefaults.large)
        Spacer(modifier = Modifier.height(8.dp))
        ObIconTextButton("Unsubscribe", R.drawable.search, sizes = OButtonDefaults.large, colors = OButtonDefaults.dangerGhost)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFeedSettingSheetContent() {
    FeedSettingSheetContent(feed = Feed.EMPTY.copy(title = "少数派", feedURL = "https://sspai.com/feed")) { }
}