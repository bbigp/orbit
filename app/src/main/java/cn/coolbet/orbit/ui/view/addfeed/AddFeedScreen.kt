package cn.coolbet.orbit.ui.view.addfeed

import android.os.Parcelable
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.kit.ObBackIconButton
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.ObIconTextField
import cn.coolbet.orbit.ui.kit.ObTopAppbar
import cn.coolbet.orbit.ui.kit.ObToastManager
import cn.coolbet.orbit.ui.kit.ToastType
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.model.domain.MetaId
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

@Parcelize
data class AddFeedScreen(
    val args: AddFeedArgs = AddFeedArgs(),
) : Screen, Parcelable {

    @Composable
    override fun Content() {
        val state = remember { AddFeedState() }
        val model = koinScreenModel<AddFeedScreenModel> { parametersOf(state) }
        val unit by model.unit.collectAsState()
        val navigator = LocalNavigator.current
        val sheetNavigator = LocalBottomSheetNavigator.current
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
        LaunchedEffect(model) {
            model.effects.collect { effect ->
                when (effect) {
                    is AddFeedEffect.Success -> ObToastManager.show(effect.message)
                    is AddFeedEffect.Error -> ObToastManager.show(effect.message, ToastType.Error)
                }
            }
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
                        ObBackIconButton(onClick = { onExit() })
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
                        value = state.inputUrl,
                        icon = R.drawable.search,
                        onValueChange = { v ->
                            state.updateInputUrl(v)
                        },
                        focusRequester = focusRequester,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                model.onAction(AddFeedAction.FetchPreview)
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.isFetchingPreview) {
                        Text("Loading preview...", style = AppTypography.R13B50)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    unit.error?.let { msg ->
                        Text(msg, style = AppTypography.R13B50)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (unit.previews.isNotEmpty()) {
                        AddFeedPreviewList(
                            items = unit.previews,
                            onItemClick = { preview ->
                                keyboardController?.hide()
                                navigator?.push(AddFeedPreviewScreen(preview))
                            },
                            onSubscribeClick = { preview, state ->
                                when (state) {
                                    AddFeedSubscribeState.NOT_SUBSCRIBED -> model.onAction(AddFeedAction.Subscribe(preview))
                                    AddFeedSubscribeState.SUBSCRIBED -> model.onAction(AddFeedAction.Unsubscribe(preview))
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
