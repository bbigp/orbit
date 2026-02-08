package cn.coolbet.orbit.ui.view.listdetail.setting

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.BottomSheetRouter
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.view.feed.EditFeedActions
import cn.coolbet.orbit.ui.view.feed.EditFeedContent
import cn.coolbet.orbit.ui.view.feed.EditFeedScreenModel
import cn.coolbet.orbit.ui.view.feed.rememberEditFeedState
import cn.coolbet.orbit.ui.view.feed.startEditFeedFlow
import cn.coolbet.orbit.ui.view.listdetail.LocalChangeLDSettings
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

@Parcelize
object ListDetailSettingScreen: Screen, Parcelable {
    private fun readResolve(): Any = ListDetailSettingScreen

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @Composable
    override fun Content() {
        val model = koinScreenModel<ListDetailSettingScreenModel>()
        val state by model.coordinator.state.collectAsState()
        val feed = state.meta as Feed
        val editState = rememberEditFeedState(feed, feed.folder)
        val editFeedModel = koinScreenModel<EditFeedScreenModel> {
            parametersOf(editState, EditFeedContent())
        }
        val actions: EditFeedActions = object: EditFeedActions {
            override fun applyChanges() = editFeedModel.applyChanges()
            override fun unsubscribe() = editFeedModel.unsubscribe()
        }

        CompositionLocalProvider(
            LocalChangeLDSettings provides model::changeLDSettings
        ) {
            Column {
                DragHandle()
                BottomSheetRouter { push, pop ->
                    LDSettingSheet(
                        meta = state.meta,
                        settings = state.settings,
                        onNavigateToEditFeed = {
                            startEditFeedFlow(
                                state = editState,
                                actions = actions,
                                cacheStore = model.cacheStore,
                                push = push,
                                pop = pop
                            )
                        }
                    )
                }
            }

        }
    }

}