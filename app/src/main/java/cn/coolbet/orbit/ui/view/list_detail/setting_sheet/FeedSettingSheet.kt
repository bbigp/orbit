package cn.coolbet.orbit.ui.view.list_detail.setting_sheet

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.kit.ListTileChevronUpDown
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.ObIconTextButton
import cn.coolbet.orbit.ui.kit.ObTextButton
import cn.coolbet.orbit.ui.kit.ObTextField
import cn.coolbet.orbit.ui.kit.ObTextFieldDefaults
import cn.coolbet.orbit.ui.kit.SheetTopBar
import cn.coolbet.orbit.ui.theme.ObTheme
import kotlinx.parcelize.Parcelize


@Parcelize
data class EditFeedScreen(
    val feed: Feed,
): Screen, Parcelable {
    @Composable
    override fun Content() {
        FeedSettingSheetContent(feed, {})
    }

}

@Composable
fun FeedSettingSheetContent(
    feed: Feed,
    onBack: () -> Unit,
) {
    Column {
        DragHandle()
        SheetTopBar(title = "Edit Feed", onBack = onBack)
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
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
            ObCard {
                ListTileChevronUpDown(
                    title = "文件夹", icon = R.drawable.folder_1,
                    trailing = feed.folder.title,
//                modifier = Modifier.click{ showFolderPicker = true },
                )
            }
        }
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)) {
            ObTextButton("Done", sizes = OButtonDefaults.large)
        }
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
            ObIconTextButton("Unsubscribe", R.drawable.reduce_o, sizes = OButtonDefaults.large, colors = OButtonDefaults.dangerGhost)
        }
        Spacer(modifier = Modifier.height(21.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFeedSettingSheetContent() {
    FeedSettingSheetContent(feed = Feed.EMPTY.copy(title = "少数派", feedURL = "https://sspai.com/feed")) { }
}