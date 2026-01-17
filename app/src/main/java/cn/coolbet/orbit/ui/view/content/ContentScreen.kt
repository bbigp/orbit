package cn.coolbet.orbit.ui.view.content

import android.os.Parcelable
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.openURL
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.asColorState
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObIconTextButton
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import kotlinx.coroutines.launch
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
        val scrollState = rememberScrollState()
        val context = LocalContext.current
        val entry = state.entry
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(state.entry.id) {
            coroutineScope.launch {
                scrollState.scrollTo(0)
            }
        }

        LaunchedEffect(data) {
            model.loadData(data)
        }

        CompositionLocalProvider(
            LocalToggleReaderMode provides model::toggleReaderMode,
            LocalChangeStarred provides model::changeStarred,
            LocalNextEntry provides model::nextEntry,
        ) {
            SystemBarAppearance(dark = false)
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
                    CompositionLocalProvider(
                        LocalOverscrollFactory provides null,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))
                            ArticleCoverImage(entry)
                            ArticleMeta(entry, modifier = Modifier.padding(horizontal = 16.dp))
                            ArticleHtml(state, scrollState)
                            NoMoreIndicator(height = 40.dp)
                            if (entry.url.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    ObIconTextButton(
                                        content = "View Website",
                                        icon = R.drawable.out_o,
                                        sizes = OButtonDefaults.mediumPadded.copy(horizontalPadding = 32.dp),
                                        colors = OButtonDefaults.secondary,
                                        iconOnRight = true,
                                        onClick = { openURL(context, entry.url.toUri()) }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Spacer(modifier = Modifier.height(48.dp))
                        }
                        Box(modifier = Modifier.background(Color.Transparent).wrapContentSize()) {
                            Text("${scrollState.value}    ${scrollState.maxValue}")
                        }
                    }
                }
            }
        }
    }
}