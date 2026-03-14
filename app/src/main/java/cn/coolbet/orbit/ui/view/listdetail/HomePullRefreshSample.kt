package cn.coolbet.orbit.ui.view.listdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun HomePullRefreshSample(
    items: List<String>,
    onRefresh: () -> Unit,
    onLongPull: () -> Unit
) {
    val listState = rememberLazyListState()
    var refreshing by remember { mutableStateOf(false) }

    TwoStagePullRefreshLayout(
        isRefreshing = refreshing,
        onRefresh = {
            refreshing = true
            onRefresh()
            // In production, set this back to false after the network refresh completes.
            refreshing = false
        },
        secondStageAction = onLongPull,
        canPullDown = {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(items) { item ->
                Text(text = item)
            }
        }
    }
}
