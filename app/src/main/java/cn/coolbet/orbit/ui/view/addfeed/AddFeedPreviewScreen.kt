package cn.coolbet.orbit.ui.view.addfeed

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.kit.DragHandleArrow
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObAsyncTextButton
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.ObTopAppbar
import cn.coolbet.orbit.ui.kit.rememberListScrollState
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.BgSecondary
import cn.coolbet.orbit.ui.view.feed.EditFeedSheet
import cn.coolbet.orbit.ui.view.home.LocalUnreadState
import cn.coolbet.orbit.ui.view.listdetail.ListDetailActions
import cn.coolbet.orbit.ui.view.listdetail.LocalListDetailActions
import cn.coolbet.orbit.ui.view.listdetail.component.LDItemList
import cn.coolbet.orbit.ui.view.listdetail.component.LDItemListState
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
        var expanded by remember { mutableStateOf(true) }

        Scaffold(
            topBar = {
                ObTopAppbar(
                    navigationIcon = {
                        ObIcon(
                            id = R.drawable.arrow_left,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .click { actions.onBack() }
                        )
                    },
                    title = {
                        Row(
                            modifier = Modifier.graphicsLayer { alpha = scrollState.progress },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                preview.title,
                                maxLines = 1,
                                style = AppTypography.M17
                            )
                        }
                    },
                )
            }
        ) { paddingValues ->
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
                            enablePullToRefresh = false
                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE8E8E8))
                ) {
                    AnimatedVisibility(
                        visible = expanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        EditFeedSheet(
                            feed = meta,
                            dragArrow = DragHandleArrow.DOWN,
                            onDragClick = { expanded = !expanded }
                        ).Content()
                    }
                    if (!expanded) {
                        Column {
                            Box(modifier = Modifier.click { expanded = !expanded }) {
                                DragHandle(arrow = DragHandleArrow.DOWN)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                                ObAsyncTextButton(
                                    "Done",
                                    sizes = OButtonDefaults.large,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class AddFeedPreviewListState(
    override val meta: Meta,
    override val settings: LDSettings = LDSettings.defaultSettings,
    override val isRefreshing: Boolean = false,
    override val hasMore: Boolean = false,
): LDItemListState
