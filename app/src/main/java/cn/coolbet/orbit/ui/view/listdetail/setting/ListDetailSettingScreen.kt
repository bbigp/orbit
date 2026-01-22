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
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.view.feed.EditFeedView
import cn.coolbet.orbit.ui.view.listdetail.LocalChangeLDSettings
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

@Parcelize
data class ListDetailSettingScreen(
    val feed: Feed,
    val settings: LDSettings
): Screen, Parcelable {

    @Composable
    override fun Content() {
        val model = koinScreenModel<ListDetailSettingScreenModel> { parametersOf(feed, settings) }
        val state by model.state.collectAsState()
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
                            LDSettingView(
                                meta = state.feed,
                                settings = state.settings,
                                onNavigateToEditFeed = { currentPage = Page.EditFeed }
                            )
                        }
                    }
                    Page.EditFeed -> {
                        EditFeedView(
                            feed = state.feed,
                            onBack = { currentPage = Page.LDSetting }
                        )
                    }
                    Page.FolderPicker -> {

                    }
                }
            }
        }
    }

    enum class Page {
        LDSetting, EditFeed, FolderPicker
    }
}