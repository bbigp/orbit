package cn.coolbet.orbit.ui.view.content

import android.os.Parcelable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.asColorState
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.ReaderPageState
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.SystemBarAppearance
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
        val entry = state.entry
        val showReaderLoading = state.isReaderModeEnabled && entry.readableContentState == ReaderPageState.Fetching
        val showReaderFailure = state.isReaderModeEnabled && entry.readableContentState == ReaderPageState.Failure
        val density = LocalDensity.current
        val swipeThresholdPx = with(density) { 72.dp.toPx() }
        var dragX by remember(state.entry.id) { mutableFloatStateOf(0f) }
        var isWaitingForWebRender by remember { mutableStateOf(false) }
        var showWebRenderOverlay by remember { mutableStateOf(false) }
        val webRenderKey = remember(
            entry.id,
            state.isReaderModeEnabled,
            entry.readableContent,
            entry.content,
            entry.readableContentState,
        ) {
            "${entry.id}:${state.isReaderModeEnabled}:${entry.readableContentState}:${entry.readableContent.hashCode()}:${entry.content.hashCode()}"
        }

        LaunchedEffect(webRenderKey, showReaderLoading, showReaderFailure) {
            if (entry.isEmpty || showReaderLoading || showReaderFailure) {
                isWaitingForWebRender = false
                showWebRenderOverlay = false
                return@LaunchedEffect
            }

            isWaitingForWebRender = true
            showWebRenderOverlay = true
        }

        LaunchedEffect(state.entry.id) {
            scrollState.scrollTo(0)
        }

        LaunchedEffect(data, settings) {
            model.loadData(data, settings)
        }

        CompositionLocalProvider(
            LocalToggleReaderMode provides { model.onAction(ContentAction.ToggleReaderMode) },
            LocalChangeStarred provides { model.onAction(ContentAction.ChangeStarred) },
            LocalOpenNextEntry provides { model.onAction(ContentAction.OpenNextEntry) },
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
                        .pointerInput(state.entry.id, swipeThresholdPx) {
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { _, dragAmount ->
                                    dragX += dragAmount
                                },
                                onDragEnd = {
                                    when {
                                        dragX <= -swipeThresholdPx -> model.onAction(ContentAction.OpenNextEntry)
                                        dragX >= swipeThresholdPx -> model.onAction(ContentAction.OpenPreviousEntry)
                                    }
                                    dragX = 0f
                                },
                                onDragCancel = {
                                    dragX = 0f
                                }
                            )
                        }
                ) {
                    CompositionLocalProvider(
                        LocalOverscrollFactory provides null,
                    ) {
                        AnimatedContent(
                            targetState = Triple(state.entry.id, showReaderLoading, showReaderFailure),
                            transitionSpec = {
                                val enterDuration = 280
                                val exitDuration = 220
                                when (state.entryTransitionDirection) {
                                    EntryTransitionDirection.Next -> {
                                        (slideInHorizontally(
                                            animationSpec = tween(enterDuration),
                                            initialOffsetX = { fullWidth -> fullWidth }
                                        ) + fadeIn(animationSpec = tween(enterDuration)))
                                            .togetherWith(
                                                slideOutHorizontally(
                                                    animationSpec = tween(exitDuration),
                                                    targetOffsetX = { fullWidth -> -fullWidth / 3 }
                                                ) + fadeOut(animationSpec = tween(exitDuration))
                                            )
                                    }
                                    EntryTransitionDirection.Previous -> {
                                        (slideInHorizontally(
                                            animationSpec = tween(enterDuration),
                                            initialOffsetX = { fullWidth -> -fullWidth }
                                        ) + fadeIn(animationSpec = tween(enterDuration)))
                                            .togetherWith(
                                                slideOutHorizontally(
                                                    animationSpec = tween(exitDuration),
                                                    targetOffsetX = { fullWidth -> fullWidth / 3 }
                                                ) + fadeOut(animationSpec = tween(exitDuration))
                                            )
                                    }
                                    EntryTransitionDirection.None -> {
                                        fadeIn(animationSpec = tween(140))
                                            .togetherWith(fadeOut(animationSpec = tween(140)))
                                    }
                                }.using(SizeTransform(clip = false))
                            }
                        ) { animatedState ->
                            val animatedShowReaderLoading = animatedState.second
                            val animatedShowReaderFailure = animatedState.third
                            if (animatedShowReaderLoading) {
                                ReaderModeLoadingSkeleton()
                            } else if (animatedShowReaderFailure) {
                                ReaderModeFailure(
                                    onRetry = model::retryReaderMode,
                                    onExitReaderMode = { model.onAction(ContentAction.ToggleReaderMode) },
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize()
                                            .verticalScroll(scrollState)
                                    ) {
                                        Spacer(modifier = Modifier.height(20.dp))
                                        ArticleCoverImage(entry)
                                        ArticleMeta(entry, modifier = Modifier.padding(horizontal = 16.dp))
                                        ArticleHtml(
                                            state = state,
                                            scrollState = scrollState,
                                            onDomContentLoaded = { domEntryId ->
                                                if (domEntryId != state.entry.id) return@ArticleHtml
                                                if (!isWaitingForWebRender) return@ArticleHtml
                                                isWaitingForWebRender = false
                                                showWebRenderOverlay = false
                                            },
                                            onArticleRendered = {},
                                            onArticleHeightChanged = { _, _ -> }
                                        )
                                        NoMoreIndicator(height = 40.dp)
                                        ViewWebsite(entry.url)
                                        Spacer(modifier = Modifier.height(48.dp))
                                    }

                                    WebRenderLoadingOverlay(
                                        visible = showWebRenderOverlay,
                                        backgroundColor = bgColor,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
