package cn.coolbet.orbit.ui.view.addfeed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.ui.kit.ListTileChevronUpDown
import cn.coolbet.orbit.ui.kit.ObAsyncTextButton
import cn.coolbet.orbit.ui.kit.ObBackTopAppBar
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.ObTextField
import cn.coolbet.orbit.ui.kit.ObTextFieldDefaults
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.folder.FolderPickerSheet
import org.koin.core.parameter.parametersOf

object AddFeedScreen: Screen {
    private fun readResolve(): Any = AddFeedScreen

    @Composable
    override fun Content() {
        val state = remember { AddFeedState() }
        val content = remember { AddFeedContent() }
        val model = koinScreenModel<AddFeedScreenModel> { parametersOf(state, content) }
        val navigator = LocalNavigator.currentOrThrow
        val folders by model.cacheStore.foldersState.collectAsState()

        LaunchedEffect(folders) {
            if (state.category.id == Folder.EMPTY.id && folders.isNotEmpty()) {
                state.updateCategory(folders.first())
            }
        }

        Scaffold(
            topBar = {
                ObBackTopAppBar(
                    title = { Text("Add Feed", style = AppTypography.M17) }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)) {
                    ObTextField(
                        hint = "Feed URL",
                        sizes = ObTextFieldDefaults.large,
                        value = state.feedUrl,
                        onValueChange = { state.updateFeedUrl(it) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ObTextField(
                        hint = "Title (optional)",
                        sizes = ObTextFieldDefaults.large,
                        value = state.title,
                        onValueChange = { state.updateTitle(it) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ObCard {
                        ListTileChevronUpDown(
                            title = "Folder",
                            icon = R.drawable.folder_1,
                            trailing = state.category.title,
                            modifier = Modifier.click {
                                navigator.push(
                                    FolderPickerSheet(
                                        folders = folders,
                                        selectedId = state.category.id,
                                        onValueChange = { id ->
                                            state.updateCategory(model.cacheStore.folder(id))
                                            navigator.pop()
                                        }
                                    )
                                )
                            },
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    ObAsyncTextButton(
                        "Add",
                        sizes = OButtonDefaults.large,
                        isLoading = state.isAdding,
                        disable = !state.canSubmit,
                        onClick = { model.addFeed() }
                    )
                }
            }
        }
    }
}
