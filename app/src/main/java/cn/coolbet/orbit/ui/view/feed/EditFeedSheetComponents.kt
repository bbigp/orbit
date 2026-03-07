package cn.coolbet.orbit.ui.view.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.ListTileChevronUpDown
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObAsyncIconButton
import cn.coolbet.orbit.ui.kit.ObAsyncTextButton
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.ObTextField
import cn.coolbet.orbit.ui.kit.ObTextFieldDefaults
import cn.coolbet.orbit.ui.kit.SheetTopBar
import cn.coolbet.orbit.ui.theme.AppTypography

@Composable
internal fun EditFeedExpandedContent(
    feed: Feed,
    title: String,
    folderTitle: String,
    topBarBackIconId: Int,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onPickFolder: () -> Unit,
) {
    Column {
        if (feed.id != 0L) {
            SheetTopBar(
                title = "Edit Feed",
                backIconId = topBarBackIconId,
                onBack = onBack,
            )
        } else {
            Text(
                "Feed",
                maxLines = 1,
                style = AppTypography.M15B50,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, bottom = 8.dp)
            )
        }
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
                value = title,
                onValueChange = onTitleChange,
            )
        }
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
            ObCard {
                ListTileChevronUpDown(
                    title = "Folder",
                    icon = R.drawable.folder_1,
                    trailing = folderTitle,
                    modifier = Modifier.click { onPickFolder() },
                )
            }
        }
    }
}

@Composable
internal fun EditFeedBottomButtons(
    isApplying: Boolean,
    isUnsubscribing: Boolean,
    isModified: Boolean,
    onApply: () -> Unit,
    onUnsubscribe: () -> Unit,
) {
    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)) {
        ObAsyncTextButton(
            "Done",
            sizes = OButtonDefaults.large,
            isLoading = isApplying,
            disable = !isModified || isApplying,
            onClick = onApply
        )
    }
    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
        ObAsyncIconButton(
            icon = R.drawable.reduce_o,
            content = "Unsubscribe",
            isLoading = isUnsubscribing,
            disable = isUnsubscribing,
            onClick = onUnsubscribe,
            sizes = OButtonDefaults.large,
            colors = OButtonDefaults.dangerGhost,
        )
    }
}
