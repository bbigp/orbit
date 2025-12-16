package cn.coolbet.orbit.ui.view.search_entries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.ui.kit.LoadMoreIndicator
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.list_detail.item.LDMagazine
import cn.coolbet.orbit.ui.view.entry.QueryContext

@Composable
fun SearchResult(
    state: SearchEntriesState,
    listState: LazyListState
) {
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
            LDMagazine(
                item,
                modifier = Modifier.clickable{
                    NavigatorBus.push(
                        Route.Entry(
                            entry = item,
                            queryContext = QueryContext.search
                        )
                    )
                }
            )
            SpacerDivider(start = 16.dp, end = 16.dp)
        }
        item(key = "indicator") {
            if (state.hasMore) LoadMoreIndicator() else NoMoreIndicator()
        }
    }
}