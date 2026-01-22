package cn.coolbet.orbit.ui.view.listdetail

import android.annotation.SuppressLint
import android.os.Build
import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.graphicsLayer
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
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.domain.UnreadMark
import cn.coolbet.orbit.ui.kit.ListLoadMoreHandler
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.ObIconGroup
import cn.coolbet.orbit.ui.kit.ObTopAppbar
import cn.coolbet.orbit.ui.kit.rememberListScrollState
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.view.home.LocalUnreadState
import cn.coolbet.orbit.ui.view.listdetail.component.LDItemList
import cn.coolbet.orbit.ui.view.listdetail.component.skeleton.LDSkeletonList
import cn.coolbet.orbit.ui.view.listdetail.component.item.unavailable.LDCUEmptyView
import cn.coolbet.orbit.ui.view.listdetail.setting.ListDetailSettingScreen
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

@Parcelize
data class ListDetailScreen(
    val metaId: MetaId,
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
        val scrollState = rememberListScrollState(
            onRefresh = { model.refresh() },
            onLoadMore = { model.nextPage() }
        )
        val navigator = LocalNavigator.current
        val actions = remember(model) {
            object : ListDetailActions {
                override fun toggleRead(entry: Entry) = model.toggleReadStatus(entry)
                override fun onBack() {
                    model.coordinator.clear()
                    navigator?.pop()
                }
            }
        }

        BackHandler(onBack = { actions.onBack() })

        LaunchedEffect(Unit) {
            model.coordinator.unfreeze()
        }
        ListLoadMoreHandler(scrollState, state)

        val sheetNavigator = LocalBottomSheetNavigator.current

        Scaffold(
            topBar = {
                ObTopAppbar(
                    navigationIcon = {
                        ObIcon(
                            id = R.drawable.arrow_left,
                            modifier = Modifier.clickable(onClick = { actions.onBack() })
                        )
                    },
                    title = {
                        Row(
                            modifier = Modifier.graphicsLayer { alpha = scrollState.progress },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                state.meta.title,
                                maxLines = 1,
                                style = AppTypography.M17
                            )
                            if (unreadMark == UnreadMark.NUMBER) {
                                Box(
                                    modifier = Modifier.padding(start = 8.dp)
                                        .background(Black08, shape = RoundedCornerShape(20.dp))
                                ) {
                                    Text(
                                        (unreadCountMap[metaId.toString()] ?: 0).toBadgeText,
                                        maxLines = 1,
                                        style = AppTypography.M13B25,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        ObIconGroup {
                            ObIcon(
                                R.drawable.search,
                                modifier = Modifier.clickable { NavigatorBus.push(Route.SearchEntries(state.meta)) },
                            )
                            ObIcon(
                                R.drawable.more,
                                modifier = Modifier.clickable {
                                    sheetNavigator.show(
                                        ListDetailSettingScreen(state.meta as Feed, state.settings)
                                    )
                                },
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
                    .fillMaxSize()
            ) {
                when(state.state) {
                    is LoadingState.Loading -> { LDSkeletonList() }
                    is LoadingState.Empty -> { LDCUEmptyView() }
                    else -> {
                        CompositionLocalProvider(
                            LocalOverscrollFactory provides null,
                            LocalListDetailActions provides actions,
                            LocalUnreadState provides unreadState,
                        ) {
                            LDItemList(
                                scrollState = scrollState,
                                state = state,
                                groupedData = groupedItems
                            )
                        }
                    }
                }
            }
        }
    }

}