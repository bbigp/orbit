package cn.coolbet.orbit.ui.view.feed

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.ui.kit.ListTileChevronUpDown
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObAsyncIconButton
import cn.coolbet.orbit.ui.kit.ObAsyncTextButton
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.ObTextField
import cn.coolbet.orbit.ui.kit.ObTextFieldDefaults
import cn.coolbet.orbit.ui.kit.Pop
import cn.coolbet.orbit.ui.kit.Push
import cn.coolbet.orbit.ui.kit.SheetTopBar
import cn.coolbet.orbit.ui.view.folder.FolderPickerSheet

fun startEditFeedFlow(
    state: EditFeedState,
    actions: EditFeedActions,
    cacheStore: CacheStore,
    push: Push,
    pop: Pop
) {

    push {

        EditFeedSheet(
            state = state,
            onBack = pop,
            actions = actions,
            onNavigateToFolderPicker = {
                push {
                    val folders by cacheStore.foldersState.collectAsState()
                    FolderPickerSheet(
                        folders = folders,
                        selectedValue = state.category.id,
                        onValueChange = { id ->
                            state.updateCategory(cacheStore.folder(id)) // 修改状态
                            pop()
                        },
                        onBack = pop
                    )
                }
            }
        )
    }


}

@Composable
fun EditFeedSheet(
    state: EditFeedState,
    actions: EditFeedActions,
    onBack: () -> Unit = {},
    onNavigateToFolderPicker: () -> Unit = {}
) {
    Log.d("EditFeedSheet", "EditFeedSheet recomposing, category.title: ${state.category.title}")
    Column {
        SheetTopBar(title = "Edit Feed", onBack = onBack)
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            ObTextField(
                sizes = ObTextFieldDefaults.large,
                value = state.feed.feedURL,
                readOnly = true,
            )
        }
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
            ObTextField(
                sizes = ObTextFieldDefaults.large,
                value = state.title,
                onValueChange = { state.updateTitle(it) },
            )
        }
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
            ObCard {
                ListTileChevronUpDown(
                    title = "文件夹", icon = R.drawable.folder_1,
                    trailing = state.category.title,
                    modifier = Modifier.click{ onNavigateToFolderPicker() },
                )
            }
        }
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)) {
            ObAsyncTextButton(
                "Done",
                sizes = OButtonDefaults.large,
                isLoading = state.isApplying,
                disable = !state.isModified || state.isApplying,
                onClick = { actions.applyChanges() }
            )
        }
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
            ObAsyncIconButton(
                icon = R.drawable.reduce_o,
                content = "Unsubscribe",
                isLoading = state.isUnsubscribing,
                disable = state.isUnsubscribing,
                onClick = { actions.unsubscribe() },
                sizes = OButtonDefaults.large,
                colors = OButtonDefaults.dangerGhost,
            )
        }
        Spacer(modifier = Modifier.height(21.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditFeedSheet() {
    val state = remember {
        EditFeedState(
            Feed.EMPTY.copy(title = "少数派", feedURL = "https://sspai.com/feed"),
            Folder.EMPTY)
    }
    EditFeedSheet(state = state, actions = object : EditFeedActions {
        override fun applyChanges() {
        }

        override fun unsubscribe() {
        }

    }) { }
}