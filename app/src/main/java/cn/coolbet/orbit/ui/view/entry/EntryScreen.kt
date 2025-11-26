package cn.coolbet.orbit.ui.view.entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.model.domain.Entry

data class EntryScreen(
    val entry: Entry
): Screen {

    @Composable
    override fun Content() {
        val model = getScreenModel<EntryScreenModel, EntryScreenModel.Factory>{ factory ->
            factory.create(entry)
        }
        model

        Scaffold(
            bottomBar = { EntryBottomBar() }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues))
        }
    }
}