package cn.coolbet.orbit.ui.view.listdetail

import android.annotation.SuppressLint
import android.os.Build
import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.common.toBadgeText
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.LoadingState
import cn.coolbet.orbit.manager.asUnreadMarkState
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.domain.UnreadMark
import cn.coolbet.orbit.ui.kit.ObBackIconButton
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.ObIconGroup
import cn.coolbet.orbit.ui.kit.ObTopAppbar
import cn.coolbet.orbit.ui.kit.PinnedTopBarLayout
import cn.coolbet.orbit.ui.kit.showAnimated
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.view.home.LocalUnreadState
import cn.coolbet.orbit.ui.view.listdetail.component.LDItemList
import cn.coolbet.orbit.ui.view.listdetail.component.skeleton.LDSkeletonList
import cn.coolbet.orbit.ui.view.listdetail.component.unavailable.LDCUEmptyView
import cn.coolbet.orbit.ui.view.listdetail.setting.LDSettingSheet
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

@Parcelize
data class ListDetailScreen(
    val metaId: MetaId,
    val config: ListDetailConfig = ListDetailConfig(),
): Screen, Parcelable {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("FrequentlyChangingValue")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = koinScreenModel<ListDetailScreenModel> { parametersOf(metaId) }
        val groupedItems by model.groupedItemsFlow.collectAsState()
        val state by model.coordinator.state.collectAsState()
        val unreadState = model.unreadMapState.collectAsState()
        val unreadCountMap by unreadState
        val unreadMark by Env.settings.unreadMark.asUnreadMarkState()
        val listState = rememberLazyListState()
        val navigator = LocalNavigator.current
        val sheetNavigator = LocalBottomSheetNavigator.current
        val actions = remember(model, sheetNavigator, navigator) {
            object : ListDetailActions {
                override fun toggleRead(entry: Entry) = model.toggleReadStatus(entry)
                override fun onBack() {
                    if (sheetNavigator.isVisible) {
                        sheetNavigator.hide()
                        return
                    }
                    model.coordinator.clear()
                    navigator?.pop()
                }
            }
        }

        BackHandler(onBack = { actions.onBack() })

        LaunchedEffect(Unit) {
            model.coordinator.unfreeze()
        }

        Scaffold(
            contentWindowInsets = WindowInsets.navigationBars
        ) { paddingValues ->
            PinnedTopBarLayout(
                modifier = Modifier.padding(paddingValues),
                listState = listState,
                topBar = { collapsed ->
                    ObTopAppbar(
                        navigationIcon = { ObBackIconButton(onClick = { actions.onBack() }) },
                        title = {
                            ListDetailTopBarTitle(
                                title = state.meta.title,
                                unreadMark = unreadMark,
                                unreadCount = unreadCountMap[metaId.toString()] ?: 0,
                                collapsed = collapsed
                            )
                        },
                        actions = {
                            ObIconGroup {
                                if (config.showSearch) {
                                    ObIcon(
                                        R.drawable.search,
                                        modifier = Modifier.clickable {
                                            NavigatorBus.push(
                                                Route.SearchEntries(
                                                    state.meta
                                                )
                                            )
                                        },
                                    )
                                }
                                ObIcon(
                                    R.drawable.more,
                                    modifier = Modifier.clickable {
                                        sheetNavigator.showAnimated(LDSettingSheet)
                                    },
                                )
                            }
                        }
                    )
                },
            ) { contentPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                    when (state.state) {
                        is LoadingState.Idle -> LDSkeletonList()
                        is LoadingState.Loading -> LDSkeletonList()
                        is LoadingState.Empty -> LDCUEmptyView()
                        else -> {
                            CompositionLocalProvider(
                                LocalOverscrollFactory provides null,
                                LocalListDetailActions provides actions,
                                LocalUnreadState provides unreadState,
                            ) {
                                LDItemList(
                                    listState = listState,
                                    onRefresh = { model.refresh() },
                                    onLoadMore = { model.nextPage() },
                                    state = state,
                                    groupedData = groupedItems,
                                    enableSwipe = config.enableSwipe
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun ListDetailTopBarTitle(
    title: String,
    unreadMark: UnreadMark,
    unreadCount: Int,
    collapsed: Boolean,
) {
    AnimatedVisibility(
        visible = collapsed,
        enter = fadeIn(animationSpec = tween(200)) + scaleIn(
            animationSpec = tween(200),
            initialScale = 0.95f
        ),
        exit = fadeOut(animationSpec = tween(200)) + scaleOut(
            animationSpec = tween(200),
            targetScale = 0.95f
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, maxLines = 1, style = AppTypography.M17)
            Spacer(modifier = Modifier.width(8.dp))
            if (unreadMark == UnreadMark.NUMBER) {
                Text(
                    unreadCount.toBadgeText,
                    maxLines = 1,
                    style = AppTypography.M13B25,
                    modifier = Modifier
                        .background(Black08, shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)

                )
            }
        }
    }
}
