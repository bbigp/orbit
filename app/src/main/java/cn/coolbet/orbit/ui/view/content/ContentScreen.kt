package cn.coolbet.orbit.ui.view.content

import android.os.Parcelable
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.asColorState
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContentScreen(
    val data: Entry,
    val settings: LDSettings,
): Screen, Parcelable {

    @Composable
    override fun Content() {
        val model = koinScreenModel<ContentScreenModel>()
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

        LaunchedEffect(data, settings) {
            model.loadData(data, settings)
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
                            ViewWebsite(entry.url)
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