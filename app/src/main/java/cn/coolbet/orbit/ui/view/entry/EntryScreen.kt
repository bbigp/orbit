package cn.coolbet.orbit.ui.view.entry

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import kotlinx.parcelize.Parcelize

@Parcelize
data class EntryScreen(
    val data: Entry,
    val queryContext: QueryContext
): Screen, Parcelable {

    @Composable
    override fun Content() {
        val model = getScreenModel<EntryScreenModel, EntryScreenModel.Factory>{ factory ->
            factory.create(queryContext)
        }
        val state by model.state.collectAsState()

        LaunchedEffect(data) {
            model.loadData(data)
        }

        CompositionLocalProvider(
            LocalChangeReaderView provides model::changeDisplayMode,
            LocalChangeStarred provides model::changeStarred,
            LocalNextEntry provides model::nextEntry
        ) {
            Scaffold(
                bottomBar = { EntryBottomBar(state, queryContext) }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.padding(paddingValues)
                        .fillMaxSize()
                ) {
                    val entry = state.entry
                    if (state.readingModeEnabled && !state.isLoadingReadableContent) {
                        LaunchedEffect(entry.url) {
                            model.startLoading()
                        }
                    }

                    if (state.isLoadingReadableContent) {
                        ReaderView(
                            key = state.entry.id,
                            url = state.entry.url,
                            onContentExtracted = { extracted, id ->
                                model.updateReadableContent(
                                    extracted.content ?: "",
                                    extracted.leadImageUrl ?: "",
                                    extracted.excerpt ?: "",
                                    id
                                )
                            }
                        )
                        Box(modifier = Modifier.fillMaxSize()) {
                            LoadMoreIndicator()
                        }
                    } else {
                        EntryView(state)
                    }
                }
            }
        }
    }
}