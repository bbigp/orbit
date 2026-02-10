package cn.coolbet.orbit.ui.view.profile

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.asOpenContentState
import cn.coolbet.orbit.manager.asUnreadMarkState
import cn.coolbet.orbit.model.domain.GenerateMenuItems
import cn.coolbet.orbit.model.domain.OpenContentWith
import cn.coolbet.orbit.model.domain.UnreadMark
import cn.coolbet.orbit.ui.kit.ListTileChevronUpDown
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.core.screen.Screen
import cn.coolbet.orbit.ui.kit.ListTileSwitch
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObBackTopAppBar
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.ObTextButton
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.kit.SystemBarStyleModern
import cn.coolbet.orbit.ui.theme.ObTheme
import cn.coolbet.orbit.ui.view.folder.FolderPickerSheet

object ProfileScreen: Screen {
    private fun readResolve(): Any = ProfileScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = koinScreenModel<ProfileScreenModel>()
        val state by model.state.collectAsState()
        val folders by model.cacheStore.foldersState.collectAsState()
        val unreadMark by Env.settings.unreadMark.asUnreadMarkState()
        val openContentWith by Env.settings.openContentWith.asOpenContentState()
        val autoRead by Env.settings.autoRead.asState()
        val rootFolderId by Env.settings.rootFolder.asState()

        var showOpenContentWithMenus by remember { mutableStateOf(false) }
        var showUnreadMarkMenus by remember { mutableStateOf(false) }
        val sheetNavigator = LocalBottomSheetNavigator.current


        SystemBarStyleModern(statusBarColor = ObTheme.colors.secondaryContainer, isLightStatusBars = false)
        Scaffold (
            containerColor = ObTheme.colors.secondaryContainer,
            topBar = {
                ObBackTopAppBar(
                    background = ObTheme.colors.secondaryContainer,
//                    title = "设置",
                )
            }
        ) { paddingValues ->
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
            ) {
                item { ProfileInfo(user = state.user) }
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    ObCard {
                        ListTileChevronUpDown(
                            title = "未读标记", icon = R.drawable.unread_dashed,
                            trailing = unreadMark.value,
                            showMenu = showUnreadMarkMenus,
                            menuContent = {
                                UnreadMark.GenerateMenuItems(unreadMark) { mark ->
                                    Env.settings.unreadMark.value = mark.value
                                    showUnreadMarkMenus = false
                                }
                            }
                        )
                        SpacerDivider(start = 52.dp, end = 12.dp)
                        ListTileChevronUpDown(
                            title = "默认打开方式", icon = R.drawable.page,
                            trailing = openContentWith.value,
                            showMenu = showOpenContentWithMenus,
                            menuContent = {
                                OpenContentWith.GenerateMenuItems(
                                    selectedValue = openContentWith,
                                    filterValue = OpenContentWith.Default
                                ) { with ->
                                    Env.settings.openContentWith.value = with.value
                                    showOpenContentWithMenus = false
                                }
                            }
                        )
                        SpacerDivider(start = 52.dp, end = 12.dp)
                        ListTileSwitch(
                            title = "自动已读", icon = R.drawable.check_o,
                            checked = autoRead,
                            onCheckedChange = { v->
                                Env.settings.autoRead.value = v
                            }
                        )
                        SpacerDivider(start = 52.dp, end = 12.dp)
                        ListTileChevronUpDown(
                            title = "根文件夹", icon = R.drawable.folder_1,
                            trailing = state.rootFolder.title,
                            modifier = Modifier.click{
                                sheetNavigator.show(
                                    FolderPickerSheet(
                                        folders = folders,
                                        selectedId = rootFolderId,
                                        onValueChange = { id ->
                                            Env.settings.rootFolder.value = id
                                            sheetNavigator.hide()
                                        },
                                    )
                                )
                            },
                        )
                    }
                }
                item {  Spacer(modifier = Modifier.height(16.dp)) }
                item {
                    ObCard {
                        SyncSubscriptions("01:01")
                    }
                }
                item {  Spacer(modifier = Modifier.height(24.dp)) }

                item {
                    Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ObTextButton(
                            "清除数据",
                            onClick = { model.deleteLocalData() }
                        )
                    }
                }
                item {
                    ObTextButton(
                        "退出登录",
                        colors = OButtonDefaults.dangerGhost,
                        onClick = { model.logout() }
                    )
                }
            }
        }
    }


}


