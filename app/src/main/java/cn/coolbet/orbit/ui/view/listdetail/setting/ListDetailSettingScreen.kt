package cn.coolbet.orbit.ui.view.listdetail.setting

import android.os.Parcelable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.view.feed.EditFeedScreenModel
import cn.coolbet.orbit.ui.view.feed.EditFeedSheet
import cn.coolbet.orbit.ui.view.folder.FolderPickerSheet
import cn.coolbet.orbit.ui.view.listdetail.LocalChangeLDSettings
import kotlinx.parcelize.Parcelize

@Parcelize
object ListDetailSettingScreen: Screen, Parcelable {
    private fun readResolve(): Any = ListDetailSettingScreen

    @Composable
    override fun Content() {
        val model = koinScreenModel<ListDetailSettingScreenModel>()
        val editFeedModel = koinScreenModel<EditFeedScreenModel>()
        val state by model.coordinator.state.collectAsState()
        val folders by model.cacheStore.foldersState.collectAsState()
        var currentPage by remember { mutableStateOf(Page.LDSetting) }

        Column {
            DragHandle()
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState == Page.EditFeed || targetState == Page.FolderPicker) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(slideOutHorizontally { width -> width } + fadeOut())
                    }
                },
                label = "SheetContentAnimation"
            ) { targetPage ->
                when(targetPage) {
                    Page.LDSetting -> {
                        CompositionLocalProvider(LocalChangeLDSettings provides model::changeLDSettings) {
                            LDSettingSheet(
                                meta = state.meta,
                                settings = state.settings,
                                onNavigateToEditFeed = { currentPage = Page.EditFeed }
                            )
                        }
                    }
                    Page.EditFeed -> {
                        EditFeedSheet(
                            feed = state.meta as Feed,
                            onBack = { currentPage = Page.LDSetting },
                            onNavigateToFolderPicker = { currentPage = Page.FolderPicker }
                        )
                    }
                    Page.FolderPicker -> {
                        FolderPickerSheet(
                            folders = folders,
                            selectedValue = (state.meta as Feed).folderId,
                            onValueChange = { folderId ->
                                editFeedModel.submit(folderId, state.meta.id)
                            },
                            onBack = { currentPage = Page.EditFeed },
                        )
                    }
                }
            }
        }
    }

    enum class Page {
        LDSetting, EditFeed, FolderPicker
    }
}