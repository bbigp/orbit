package cn.coolbet.orbit.ui.view.search_entries

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.ui.kit.InfiniteScrollHandler
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObTextButton
import cn.coolbet.orbit.ui.kit.ObTextFieldAppbar
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchEntriesScreen(
    val meta: Meta,
): Screen, Parcelable {

    @Composable
    override fun Content() {
        val model = getScreenModel<SearchEntriesScreenModel, SearchEntriesScreenModel.Factory> { factory ->
            factory.create(meta)
        }
        val state by model.state.collectAsState()
        val ldState by model.coordinator.state.collectAsState()
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val listState = rememberLazyListState()
        val navigator = LocalNavigator.current
        var showSearchResult by rememberSaveable { mutableStateOf(false) }
        var search by rememberSaveable { mutableStateOf("") }

        val onBack: () -> Unit = {
            focusManager.clearFocus()
            model.coordinator.restoreSnapshot()
            navigator?.pop()
        }

        LaunchedEffect(Unit) {
            if (search.isEmpty()) {
                focusRequester.requestFocus()
            } else {
                focusManager.clearFocus()
            }
        }

        BackHandler(onBack = onBack)

        InfiniteScrollHandler(
            listState = listState,
            stateFlow = model.coordinator.state,
            onLoadMore = {
                model.nextPage()
            }
        )

        Scaffold(
            topBar = {
                ObTextFieldAppbar(
                    value = search,
                    icon = R.drawable.search,
                    button = {
                        ObTextButton(
                            "取消",
                            colors = OButtonDefaults.ghost,
                            sizes = OButtonDefaults.mediumPadded.copy(horizontalPadding = 0.1.dp),
                            onClick = onBack
                        )
                    },
                    onValueChange = { v ->
                        if (v == "") {
                            model.coordinator.reset()
                            search = ""
                            showSearchResult = false
                            focusRequester.requestFocus()
                        } else {
                            search = v
                        }
                    },
                    focusRequester = focusRequester,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            model.load(search)
                            showSearchResult = true
                        }
                    )
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (showSearchResult) {
                    if (ldState.items.isNotEmpty()) {
                        SearchResult(ldState, listState)
                    } else {
                        NoSearch(hint = "No result found")
                    }
                } else {
                    if (state.histories.isEmpty()) {
                        NoSearch("Search in ${model.meta.title}")
                    } else {
                        SearchList(
                            state.histories,
                            onClick = { word ->
                                search = word
                                model.load(word)
                                showSearchResult = true
                            },
                            deleteSearchList = { model.deleteHistories() }
                        )
                    }
                }
            }
        }

    }

}