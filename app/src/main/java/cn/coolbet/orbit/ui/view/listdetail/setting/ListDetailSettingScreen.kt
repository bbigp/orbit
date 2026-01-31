package cn.coolbet.orbit.ui.view.listdetail.setting

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.BottomSheetRouter
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.view.feed.EditFeedScreenModel
import cn.coolbet.orbit.ui.view.feed.EditFeedSheet
import cn.coolbet.orbit.ui.view.folder.FolderPickerSheet
import cn.coolbet.orbit.ui.view.listdetail.LocalChangeLDSettings
import kotlinx.parcelize.Parcelize

@Parcelize
object ListDetailSettingScreen: Screen, Parcelable {
    private fun readResolve(): Any = ListDetailSettingScreen

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @Composable
    override fun Content() {
        val model = koinScreenModel<ListDetailSettingScreenModel>()
        val editFeedModel = koinScreenModel<EditFeedScreenModel>()
        val state by model.coordinator.state.collectAsState()
        val folders by model.cacheStore.foldersState.collectAsState()
        var feedFolder by remember { mutableStateOf((state.meta as Feed).folder) }


        CompositionLocalProvider(LocalChangeLDSettings provides model::changeLDSettings) {
            Column {
                DragHandle()
                BottomSheetRouter { push, pop ->
                    LDSettingSheet(
                        meta = state.meta,
                        settings = state.settings,
                        onNavigateToEditFeed = {
                            push {
                                EditFeedSheet(
                                    feed = state.meta as Feed,
                                    feedFolder = feedFolder,
                                    onBack = {
                                        feedFolder = (state.meta as Feed).folder
                                        pop()
                                    },
                                    onNavigateToFolderPicker = {
                                        push {
                                            FolderPickerSheet(
                                                folders = folders,
                                                selectedValue = feedFolder.id,
                                                onValueChange = { id ->
                                                    feedFolder = model.cacheStore.folder(id)
                                                    pop()
                                                },
                                                onBack = { pop() },
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    )
                }
            }

        }
    }

}