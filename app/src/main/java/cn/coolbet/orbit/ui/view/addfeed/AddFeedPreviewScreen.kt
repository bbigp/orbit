package cn.coolbet.orbit.ui.view.addfeed

import android.os.Parcelable
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObAsyncTextButton
import cn.coolbet.orbit.ui.kit.ObTextButton
import cn.coolbet.orbit.ui.kit.ObToastManager
import cn.coolbet.orbit.ui.kit.SimplePagingLoadState
import cn.coolbet.orbit.ui.kit.ToastType
import cn.coolbet.orbit.ui.kit.showAnimated
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.feed.EditFeedArgs
import cn.coolbet.orbit.ui.view.feed.EditFeedSheet
import cn.coolbet.orbit.ui.view.home.LocalUnreadState
import cn.coolbet.orbit.ui.view.listdetail.ListDetailActions
import cn.coolbet.orbit.ui.view.listdetail.LocalListDetailActions
import cn.coolbet.orbit.ui.view.listdetail.component.LDItemList
import cn.coolbet.orbit.ui.view.listdetail.component.unavailable.LDCUEmptyView
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

@Parcelize
data class AddFeedPreviewScreen(
    val preview: AddFeedPreview,
    val args: AddFeedPreviewArgs = AddFeedPreviewArgs(),
) : Screen, Parcelable {

    @Composable
    override fun Content() {
        val state = remember(preview) { AddFeedPreviewState(preview) }
        val model = koinScreenModel<AddFeedPreviewScreenModel> { parametersOf(state) }
        val navigator = LocalNavigator.current
        val sheetNavigator = LocalBottomSheetNavigator.current
        val listState = remember(state.feed) { AddFeedPreviewListState(meta = state.feed) }
        val pagingState = remember { SimplePagingLoadState() }
        val lazyListState = rememberLazyListState()
        val actions = remember {
            object : ListDetailActions {
                override fun toggleRead(entry: Entry) {}
                override fun onBack() { navigator?.pop() }
            }
        }

        LaunchedEffect(model) {
            model.effects.collect { effect ->
                when (effect) {
                    is AddFeedPreviewEffect.Subscribed -> {
                        navigator?.pop()
                        delay(120)
                        ObToastManager.show("Subscribed")
                    }
                    is AddFeedPreviewEffect.Error -> {
                        ObToastManager.show(effect.message, ToastType.Error)
                    }
                }
            }
        }

        Scaffold { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                if (state.preview.entries.isEmpty()) {
                    LDCUEmptyView()
                } else {
                    CompositionLocalProvider(
                        LocalOverscrollFactory provides null,
                        LocalListDetailActions provides actions,
                        LocalUnreadState provides remember { mutableStateOf(emptyMap()) },
                    ) {
                        LDItemList(
                            listState = lazyListState,
                            onRefresh = {},
                            onLoadMore = {},
                            state = listState,
                            pagingState = pagingState,
                            groupedData = mapOf("" to state.preview.entries),
                            enablePullToRefresh = false,
                            enableSwipe = false
                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 10.dp, vertical = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE8E8E8))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = state.feed.title,
                            maxLines = 1,
                            style = AppTypography.M17,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                        Row {
                            Box(modifier = Modifier.weight(1f)) {
                                ObTextButton(
                                    content = if (state.feed.id > 0L) "Done" else "Cancel",
                                    sizes = OButtonDefaults.medium,
                                    colors = OButtonDefaults.secondary,
                                    onClick = { navigator?.pop() }
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                ObAsyncTextButton(
                                    content = if (state.feed.id > 0L) "More" else "Subscribe",
                                    sizes = OButtonDefaults.medium,
                                    isLoading = state.isSubmitting,
                                    disable = state.isSubmitting,
                                    onClick = {
                                        if (state.feed.id > 0L) {
                                            sheetNavigator.showAnimated(
                                                EditFeedSheet(
                                                    state.feed,
                                                    args = EditFeedArgs(topBarBackIconId = R.drawable.x),
                                                )
                                            )
                                        } else {
                                            model.onAction(AddFeedPreviewAction.Subscribe)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
