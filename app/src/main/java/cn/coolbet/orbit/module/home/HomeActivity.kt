package cn.coolbet.orbit.module.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.theme.OrbitTheme

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContent {
            OrbitTheme {
                HomePage()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()

    Scaffold {
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = viewModel::load,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LazyColumn (
                modifier = Modifier.fillMaxWidth(),
                state = lazyListState,
            ) {
                items(state.feeds, key = { it.id }) { item ->
                    FeedTile()
                }

                if (!state.hasMore) {
                    item {
                        NoMoreIndicator()
                    }
                }
            }
        }
    }
}


