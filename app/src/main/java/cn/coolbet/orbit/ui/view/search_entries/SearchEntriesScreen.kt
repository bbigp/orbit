package cn.coolbet.orbit.ui.view.search_entries

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.NavigatorBus
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
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val listState = rememberLazyListState()

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        InfiniteScrollHandler(
            listState = listState,
            stateFlow = model.state,
            onLoadMore = {
                model.nextPage()
            }
        )

        Scaffold(
            topBar = {
                ObTextFieldAppbar(
                    value = state.search,
                    icon = R.drawable.search,
                    button = {
                        ObTextButton(
                            "取消",
                            colors = OButtonDefaults.ghost,
                            sizes = OButtonDefaults.mediumPadded.copy(horizontalPadding = 0.1.dp),
                            onClick = {
                                NavigatorBus.pop()
                            }
                        )
                    },
                    onValueChange = { v ->
                        if (v == "") {
                            model.clearSearchResult()
                            focusRequester.requestFocus()
                        } else {
                            model.input(v)
                        }
                    },
                    focusRequester = focusRequester,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            model.load(state.search)
                        }
                    )
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (state.page == 0) {
                    if (state.histories.isEmpty()) {
                        NoSearch("Search in ${state.meta.title}")
                    } else {
                        SearchList(
                            state.histories,
                            onClick = { word -> model.load(word) },
                            deleteSearchList = { model.deleteHistories() }
                        )
                    }
                } else {
                    if (state.items.isNotEmpty()) {
                        SearchResult(state, listState)
                    } else {
                        NoSearch(hint = "No result found")
                    }
                }
            }
        }

    }

}