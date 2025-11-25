package cn.coolbet.orbit.ui.view.search_entries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.model.domain.Meta

data class SearchEntriesScreen(
    val meta: Meta,
): Screen {

    @Composable
    override fun Content() {
        val model = getScreenModel<SearchEntriesScreenModel, SearchEntriesScreenModel.Factory> { factory ->
            factory.create(meta)
        }

        Scaffold { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {

            }
        }

    }

}