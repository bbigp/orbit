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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.view.feed.EditFeedSheet
import cn.coolbet.orbit.ui.view.listdetail.LocalChangeLDSettings
import kotlinx.parcelize.Parcelize

@Parcelize
object ListDetailSettingScreen: Screen, Parcelable {
    private fun readResolve(): Any = ListDetailSettingScreen

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @Composable
    override fun Content() {
        val model = koinScreenModel<ListDetailSettingScreenModel>()
        val state by model.coordinator.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalChangeLDSettings provides model::changeLDSettings
        ) {
            Column {
                DragHandle()
                LDSettingSheet(
                    meta = state.meta,
                    settings = state.settings,
                    onNavigateToEditFeed = {
                        navigator.push(EditFeedSheet(state.meta as Feed))
                    }
                )
            }

        }
    }

}