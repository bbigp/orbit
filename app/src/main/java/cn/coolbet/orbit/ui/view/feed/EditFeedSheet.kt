package cn.coolbet.orbit.ui.view.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.kit.DragHandleArrow
import cn.coolbet.orbit.ui.kit.ObToastManager
import cn.coolbet.orbit.ui.kit.ToastType
import cn.coolbet.orbit.ui.view.folder.FolderPickerSheet
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf

data class EditFeedSheet(
    val feed: Feed,
    val args: EditFeedArgs = EditFeedArgs(),
) : Screen {
    private val state by lazy { EditFeedState(feed) }

    @Composable
    override fun Content() {
        val model = koinScreenModel<EditFeedScreenModel> { parametersOf(state) }
        val unit by model.unit.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val sheetNavigator = LocalBottomSheetNavigator.current
        val keyboard = LocalSoftwareKeyboardController.current

        LaunchedEffect(model) {
            model.effects.collect { effect ->
                when (effect) {
                    is EditFeedEffect.Unsubscribed -> {
                        keyboard?.hide()
                        sheetNavigator.hide()
                        NavigatorBus.pop()
                        delay(120)
                        ObToastManager.show("Unsubscribed")
                    }
                    is EditFeedEffect.Error -> ObToastManager.show(effect.message, ToastType.Error)
                }
            }
        }

        val onClose: () -> Unit = {
            keyboard?.hide()
            when (args.closeMode) {
                EditFeedCloseMode.HIDE_SHEET -> sheetNavigator.hide()
                EditFeedCloseMode.POP -> navigator.pop()
            }
        }

        Column {
            when (args.dragMode) {
                EditFeedDragMode.NONE -> Unit
                EditFeedDragMode.STATIC -> DragHandle(arrow = DragHandleArrow.BAR)
            }

            EditFeedExpandedContent(
                feed = feed,
                title = state.title,
                folderTitle = state.category.title,
                topBarBackIconId = args.topBarBackIconId,
                onBack = onClose,
                onTitleChange = { v -> state.updateTitle(v) },
                onPickFolder = {
                    navigator.push(
                        FolderPickerSheet(
                            folders = unit.folders,
                            selectedId = state.category.id,
                            onValueChange = { id ->
                                unit.folders.find { it.id == id }?.let {
                                    state.updateCategory(it)
                                    navigator.pop()
                                }
                            }
                        )
                    )
                }
            )

            EditFeedBottomButtons(
                isApplying = state.isApplying,
                isUnsubscribing = state.isUnsubscribing,
                isModified = state.isModified,
                onApply = { model.onAction(EditFeedAction.ApplyChanges) },
                onUnsubscribe = { model.onAction(EditFeedAction.Unsubscribe) },
            )
            Spacer(modifier = Modifier.height(21.dp))
        }
    }
}
