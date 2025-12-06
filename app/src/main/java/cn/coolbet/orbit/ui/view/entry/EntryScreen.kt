package cn.coolbet.orbit.ui.view.entry

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.ConsumerUnit
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObIconTextButton
import kotlinx.parcelize.IgnoredOnParcel
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