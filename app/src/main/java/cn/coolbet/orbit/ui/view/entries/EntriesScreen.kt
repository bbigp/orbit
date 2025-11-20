package cn.coolbet.orbit.ui.view.entries

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel

object EntriesScreen: Screen {
    private fun readResolve(): Any = EntriesScreen

    @Composable
    override fun Content() {
        val model = getScreenModel<EntriesScreenModel>()
        val state by model.state.collectAsState()

        val listState = rememberLazyListState()

        Scaffold(

        ) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
                    .fillMaxSize()
            ) {
                if (state.isRefreshing) {
                    //todo: 骨架屏
                } else {
                    LazyColumn(
                        state = listState,
                    ) {

                    }
                }
            }
        }
    }


}