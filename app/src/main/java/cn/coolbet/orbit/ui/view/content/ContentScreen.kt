package cn.coolbet.orbit.ui.view.content

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.asColorState
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContentScreen(
    val data: Entry,
): Screen, Parcelable {

    @Composable
    override fun Content() {
        val model = getScreenModel<ContentScreenModel>()
        val state by model.state.collectAsState()
        val bgColor by Env.settings.articleBgColor.asColorState()

        LaunchedEffect(data) {
            model.loadData(data)
        }

        CompositionLocalProvider(
            LocalToggleReaderMode provides model::toggleReaderMode,
            LocalChangeStarred provides model::changeStarred,
            LocalNextEntry provides model::nextEntry,
        ) {
            SystemBarAppearance(dark = true)
            Scaffold(
                containerColor = bgColor,
                contentWindowInsets = WindowInsets(0, 0, 0,0 ),
                bottomBar = { ContentOperate(state) }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.padding(paddingValues)
                        .statusBarsPadding()
                        .fillMaxSize()
                ) {
                    val entry = state.entry
//                    if (state.readingModeEnabled && !state.isLoadingReadableContent) {
//                        LaunchedEffect(entry.url) {
//                            model.startLoading()
//                        }
//                    }
//
//                    if (state.isLoadingReadableContent) {
//                        ReaderView(
//                            key = state.entry.id,
//                            url = state.entry.url,
//                            onContentExtracted = { extracted, id ->
//                                model.updateReadableContent(
//                                    extracted.content ?: "",
//                                    extracted.leadImageUrl ?: "",
//                                    extracted.excerpt ?: "",
//                                    id
//                                )
//                            }
//                        )
//                        Box(modifier = Modifier.fillMaxSize()) {
//                            LoadMoreIndicator()
//                        }
//                    } else {
                        ArticleContent(state)
//                    }
                }
            }
        }
    }
}