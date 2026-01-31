package cn.coolbet.orbit.ui.view.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.ui.kit.ListTileChevronUpDown
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.ObIconTextButton
import cn.coolbet.orbit.ui.kit.ObTextButton
import cn.coolbet.orbit.ui.kit.ObTextField
import cn.coolbet.orbit.ui.kit.ObTextFieldDefaults
import cn.coolbet.orbit.ui.kit.SheetTopBar



@Composable
fun EditFeedSheet(
    feed: Feed,
    feedFolder: Folder,
    onBack: () -> Unit = {},
    onNavigateToFolderPicker: () -> Unit = {}
) {
    var folder by remember { mutableStateOf(feedFolder) }
    var feedTitle by remember { mutableStateOf(feed.title) }
    val isModified by remember(folder.id, feedTitle) {
        derivedStateOf { feed.folderId != folder.id || feed.title != feedTitle }
    }
    Column {
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
                    trailing = folder.title,
                    modifier = Modifier.click{ onNavigateToFolderPicker() },
                )
            }
        }
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)) {
            ObTextButton("Done", sizes = OButtonDefaults.large, disable = !isModified)
        }
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
            ObIconTextButton("Unsubscribe", R.drawable.reduce_o, sizes = OButtonDefaults.large, colors = OButtonDefaults.dangerGhost)
        }
        Spacer(modifier = Modifier.height(21.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditFeedSheet() {
    EditFeedSheet(feed = Feed.EMPTY.copy(title = "少数派", feedURL = "https://sspai.com/feed"), Folder.EMPTY) { }
}