package cn.coolbet.orbit.ui.view.addfeed

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.interaction.MutableInteractionSource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.AnimatedSlideWrapper
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.kit.DragHandleArrow
import cn.coolbet.orbit.ui.kit.rememberListScrollState
import cn.coolbet.orbit.ui.view.feed.EditFeedSheet
import cn.coolbet.orbit.ui.view.feed.EditFeedSheetConfig
import cn.coolbet.orbit.ui.view.home.LocalUnreadState
import cn.coolbet.orbit.ui.view.listdetail.ListDetailActions
import cn.coolbet.orbit.ui.view.listdetail.LocalListDetailActions
import cn.coolbet.orbit.ui.view.listdetail.component.LDItemList
import cn.coolbet.orbit.ui.view.listdetail.component.unavailable.LDCUEmptyView

data class AddFeedPreviewScreen(
    val preview: AddFeedPreview,
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val meta = remember(preview) {
            Feed.EMPTY.copy(
                title = preview.title,
                feedURL = preview.url,
                iconURL = preview.iconUrl,
            )
        }
        val state = remember(preview) {
            AddFeedPreviewListState(meta = meta)
        }
        val scrollState = rememberListScrollState(
            onRefresh = {},
            onLoadMore = {},
            hasRefreshIndicator = false
        )
        val actions = remember {
            object : ListDetailActions {
                override fun toggleRead(entry: Entry) {}
                override fun onBack() { navigator?.pop() }
            }
        }
        var expanded by rememberSaveable { mutableStateOf(true) }
        val sheetScreen = remember(meta) {
            AnimatedSlideWrapper(
                EditFeedSheet(
                    feed = meta,
                    config = EditFeedSheetConfig(
                        dragArrow = DragHandleArrow.NONE
                    )
                )
            )
        }
        val dragRotation by animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = tween(durationMillis = 220),
            label = "add_feed_drag_rotation"
        )

        Scaffold { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                if (preview.entries.isEmpty()) {
                    LDCUEmptyView()
                } else {
                    CompositionLocalProvider(
                        LocalOverscrollFactory provides null,
                        LocalListDetailActions provides actions,
                        LocalUnreadState provides remember { mutableStateOf(emptyMap()) },
                    ) {
                        LDItemList(
                            scrollState = scrollState,
                            state = state,
                            groupedData = mapOf("" to preview.entries),
                            enablePullToRefresh = false,
                            enableSwipe = false
                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE8E8E8))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .click { expanded = !expanded }
                        ) {
                            Box(modifier = Modifier.graphicsLayer { rotationZ = dragRotation }) {
                                DragHandle(arrow = DragHandleArrow.UP)
                            }
                        }
                        AnimatedContent(
                            targetState = expanded,
                            transitionSpec = {
                                ContentTransform(
                                    targetContentEnter = expandVertically() + fadeIn(),
                                    initialContentExit = shrinkVertically() + fadeOut()
                                )
                            }
                        ) { isExpanded ->
                            if (isExpanded) {
                                sheetScreen.Content()
                            } else {
                                AddFeedCollapsedHeader(
                                    title = preview.title,
                                    onExpand = { expanded = true }
                                )
                            }
                        }
                        AddFeedActionBar(
                            onCancel = { navigator?.pop() },
                            onAdd = { }
                        )
                    }
                }
            }
        }
    }
}
