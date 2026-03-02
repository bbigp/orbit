package cn.coolbet.orbit.ui.view.addfeed

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.ObIconTextField
import cn.coolbet.orbit.ui.kit.ObTopAppbar
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.listdetail.ListDetailConfig
import cn.coolbet.orbit.ui.view.listdetail.ListDetailMoreAction
import cn.coolbet.orbit.model.domain.MetaId
import org.koin.core.parameter.parametersOf

object AddFeedScreen: Screen {
    private fun readResolve(): Any = AddFeedScreen

    @Composable
    override fun Content() {
        val state = remember { AddFeedState() }
        val model = koinScreenModel<AddFeedScreenModel> { parametersOf(state) }
        val dataState by model.unit.collectAsState()
        val navigator = LocalNavigator.current
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(
                TextFieldValue(
                    "https://sspai.com/feed",
//                    "https://juejin.cn/rss",
                )
            )
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        val onExit: () -> Unit = {
            keyboardController?.hide()
            navigator?.pop()
        }

        BackHandler(onBack = onExit)

        Scaffold(
            topBar = {
                ObTopAppbar(
                    title = { Text("RSS/Atom", style = AppTypography.M17) },
                    navigationIcon = {
                        ObIcon(
                            id = R.drawable.arrow_left,
                            modifier = Modifier.click { onExit() }
                        )
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 0.dp)) {
                    ObIconTextField(
                        hint = "URL...",
                        value = text,
                        icon = R.drawable.search,
                        onValueChange = { v ->
                            text = v
                        },
                        focusRequester = focusRequester,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                model.fetchPreview(text.text)
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.isFetchingPreview) {
                        Text("Loading preview...", style = AppTypography.R13B50)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    dataState.error?.let { msg ->
                        Text(msg, style = AppTypography.R13B50)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (dataState.previews.isNotEmpty()) {
                        AddFeedPreviewList(
                            items = dataState.previews,
                            onItemClick = { preview ->
                                keyboardController?.hide()
                                if (preview.feedId > 0L) {
                                    NavigatorBus.push(
                                        Route.Entries(
                                            metaId = MetaId("e", preview.feedId),
                                            config = ListDetailConfig(
                                                showSearch = true,
                                                enableSwipe = true,
                                                moreAction = ListDetailMoreAction.OPEN_EDIT_FEED
                                            )
                                        )
                                    )
                                } else {
                                    navigator?.push(AddFeedPreviewScreen(preview))
                                }
                            },
                            onSubscribeClick = { preview, state ->
                                when (state) {
                                    AddFeedSubscribeState.NOT_SUBSCRIBED -> model.addFeed(preview)
                                    AddFeedSubscribeState.SUBSCRIBED -> model.unsubscribeFeed(preview)
                                    AddFeedSubscribeState.SUBSCRIBING -> Unit
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
