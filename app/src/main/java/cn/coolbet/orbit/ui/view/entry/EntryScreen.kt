package cn.coolbet.orbit.ui.view.entry

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.common.ConsumerUnit
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import kotlinx.parcelize.Parcelize


val LocalChangeReaderView = compositionLocalOf<ConsumerUnit> {
    error("No function provided")
}

@Parcelize
data class EntryScreen(
    val data: Entry
): Screen, Parcelable {

    @Composable
    override fun Content() {
        val model = getScreenModel<EntryScreenModel, EntryScreenModel.Factory>{ factory ->
            factory.create(data)
        }
        val state by model.state.collectAsState()

        DisposableEffect(Unit) {
            onDispose {
                model.autoRead()
            }
        }

        CompositionLocalProvider(
            LocalChangeReaderView provides model::changeDisplayMode
        ) {
            Scaffold(
                bottomBar = { EntryBottomBar(state) }
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
                            url = state.entry.url,
                            onContentExtracted = { extracted ->
                                model.updateReadableContent(
                                    extracted.content ?: "",
                                    extracted.leadImageUrl ?: "",
                                    extracted.excerpt ?: ""
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