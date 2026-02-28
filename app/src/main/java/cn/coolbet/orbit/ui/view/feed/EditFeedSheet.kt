package cn.coolbet.orbit.ui.view.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.kit.ListTileChevronUpDown
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObAsyncIconButton
import cn.coolbet.orbit.ui.kit.ObAsyncTextButton
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.ObTextField
import cn.coolbet.orbit.ui.kit.ObTextFieldDefaults
import cn.coolbet.orbit.ui.kit.SheetTopBar
import cn.coolbet.orbit.ui.view.folder.FolderPickerSheet
import org.koin.core.parameter.parametersOf

data class EditFeedSheet(val feed: Feed): Screen {

    private val state by lazy {
        EditFeedState(feed, feed.folder)
    }


    @Composable
    override fun Content() {
        val model = koinScreenModel<EditFeedScreenModel> {
            parametersOf(state, EditFeedContent())
        }
        val actions: EditFeedActions = object: EditFeedActions {
            override fun applyChanges() = model.applyChanges()
            override fun unsubscribe() = model.unsubscribe()
        }
        val navigator = LocalNavigator.currentOrThrow
        val folders by model.cacheStore.foldersState.collectAsState()
        val keyboardController = LocalSoftwareKeyboardController.current

        Column {
            DragHandle()
            SheetTopBar(title = "Edit Feed", onBack = {
                keyboardController?.hide()
                navigator.pop()
            })
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
                        modifier = Modifier.click{ navigator.push(
                            FolderPickerSheet(
                                folders = folders,
                                selectedId = state.category.id,
                                onValueChange = { id ->
                                    state.updateCategory(model.cacheStore.folder(id))
                                    navigator.pop()
                                }
                            )
                        ) },
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

}