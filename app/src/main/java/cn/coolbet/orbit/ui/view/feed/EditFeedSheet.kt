package cn.coolbet.orbit.ui.view.feed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.view.folder.FolderPickerSheet
import org.koin.core.parameter.parametersOf

data class EditFeedSheet(
    val feed: Feed,
    val config: EditFeedSheetConfig = EditFeedSheetConfig(),
): Screen {

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
        val sheetNavigator = LocalBottomSheetNavigator.current
        val folders by model.cacheStore.foldersState.collectAsState()
        val keyboardController = LocalSoftwareKeyboardController.current
        val rootFolderId = Env.settings.rootFolder.value
        val rootFolder = folders.find { it.id == rootFolderId }
        var expanded by rememberSaveable { mutableStateOf(config.expandableInitialExpanded) }
        val contentExpanded = if (config.expandable) expanded else true

        LaunchedEffect(feed.id, rootFolder?.id) {
            if (feed.id == 0L && state.category.id == 0L && rootFolder != null) {
                state.updateCategory(rootFolder)
            }
        }
        val dragRotation by animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = tween(durationMillis = 220),
            label = "edit_feed_drag_rotation"
        )

        val onClose: () -> Unit = {
            keyboardController?.hide()
            when (config.backAction) {
                EditFeedBackAction.POP_SCREEN -> navigator.pop()
                EditFeedBackAction.HIDE_SHEET -> sheetNavigator.hide()
            }
        }

        Column {
            EditFeedDrag(
                config = config,
                rotation = dragRotation,
                onToggle = { if (config.expandable) expanded = !expanded }
            )

            AnimatedContent(
                targetState = contentExpanded,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = expandVertically() + fadeIn(),
                        initialContentExit = shrinkVertically() + fadeOut()
                    )
                }
            ) { isExpanded ->
                if (isExpanded) {
                    EditFeedExpandedContent(
                        feed = feed,
                        title = state.title,
                        folderTitle = state.category.title,
                        topBarBackIconId = config.topBarBackIconId,
                        onBack = onClose,
                        onTitleChange = state::updateTitle,
                        onPickFolder = {
                            navigator.push(
                                FolderPickerSheet(
                                    folders = folders,
                                    selectedId = state.category.id,
                                    onValueChange = { id ->
                                        state.updateCategory(model.cacheStore.folder(id))
                                        navigator.pop()
                                    }
                                )
                            )
                        }
                    )
                } else {
                    EditFeedCollapsedHeader(
                        text = feed.title.ifBlank { state.title },
                        onExpand = { expanded = true }
                    )
                }
            }

            EditFeedBottomButtons(
                config = config,
                showEditButtons = feed.id != 0L,
                isApplying = state.isApplying,
                isUnsubscribing = state.isUnsubscribing,
                isModified = state.isModified,
                onApply = { actions.applyChanges() },
                onUnsubscribe = { actions.unsubscribe() },
                onCancel = {
                    if (feed.id == 0L) {
                        NavigatorBus.pop()
                    } else {
                        onClose()
                    }
                }
            )
            Spacer(modifier = Modifier.height(21.dp))
        }
    }
}
