package cn.coolbet.orbit.ui.view.search_entries

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.ui.kit.InfiniteScrollHandler
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObTextButton
import cn.coolbet.orbit.ui.kit.ObTextFieldAppbar
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black25
import cn.coolbet.orbit.ui.theme.Black50
import cn.coolbet.orbit.ui.view.entries.EntryTile

data class SearchEntriesScreen(
    val meta: Meta,
): Screen {

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


@Composable
fun SearchResult(state: SearchEntriesState, listState: LazyListState) {
    LazyColumn(
        state = listState
    ) {
        item(key = "count") {
            Text(
                "Results",
                maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R13B50,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 18.dp, end = 18.dp)
                    .height(30.dp).fillMaxWidth()
            )
        }
        items(state.items, key = { it.id }) { item ->
            EntryTile(item)
            SpacerDivider(start = 16.dp, end = 16.dp)
        }
        item(key = "indicator") {
            if (state.hasMore) LoadMoreIndicator() else NoMoreIndicator()
        }
    }
}


@Composable
fun SearchList(
    words: Set<String> = emptySet(),
    onClick: (String) -> Unit = {},
    deleteSearchList: () -> Unit  = {},
) {
    val focusManager = LocalFocusManager.current
    Column {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 12.dp, top = 4.dp, bottom = 8.dp)
                .height(36.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "最近搜索",
                maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R13B50,
                modifier = Modifier.padding(bottom = 10.dp).weight(1f)
            )
            Image(
                modifier = Modifier.size(20.dp).clickable(
                    onClick = deleteSearchList
                ),
                painter = painterResource(id = R.drawable.trash),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Black50),
            )
        }

        words.forEach { word ->
            Row(
                modifier = Modifier.height(48.dp).padding(start = 16.dp, end = 12.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            focusManager.clearFocus()
                            onClick(word)
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    word,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R15,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.chevron_right),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Black25),
                )
            }
            SpacerDivider(start = 16.dp, end = 12.dp)
        }
        NoMoreIndicator()

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchList() {
    val words = setOf("少数派", "App", "苹果")
    SearchList(words)
}




@Composable
fun NoSearch(hint: String = "Search") {
    Box(
        modifier = Modifier.padding(vertical = 120.dp).fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(56.dp),
                painter = painterResource(id = R.drawable.search_1),
                contentDescription = "",
                contentScale = ContentScale.Fit,
            )
            Text(hint, maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.M15B25)
        }
    }
}