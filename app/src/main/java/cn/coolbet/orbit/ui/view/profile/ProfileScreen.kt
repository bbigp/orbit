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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cn.coolbet.orbit.R
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.asOpenContentState
import cn.coolbet.orbit.manager.asUnreadMarkState
import cn.coolbet.orbit.model.domain.OpenContentWith
import cn.coolbet.orbit.model.domain.UnreadMark
import cn.coolbet.orbit.ui.kit.DropdownMenuDivider
import cn.coolbet.orbit.ui.kit.ListTileChevronUpDown
import cn.coolbet.orbit.ui.kit.ListTileSwitch
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObBackTopAppBar
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.ObDropdownMenuItem
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
        val model = getScreenModel<ProfileScreenModel>()
        val state by model.state.collectAsState()
        val folders by model.folders.collectAsState()
        val unreadMark by Env.settings.unreadMark.asUnreadMarkState()
        val openContentWith by Env.settings.openContentWith.asOpenContentState()
        val autoRead by Env.settings.autoRead.asState()
        val rootFolderId by Env.settings.rootFolder.asState()

        var showFolderPicker by remember { mutableStateOf(false) }
        FolderPickerSheet (
            show = showFolderPicker,
            onDismiss = {
                showFolderPicker = false
            },
            folders = folders,
            selectedValue = rootFolderId,
            onValueChange = { id ->
                Env.settings.rootFolder.value = id
                model.refreshRootFolder()
                showFolderPicker = false
            }
        )

        val unreadMarkMenus: @Composable (onClose: () -> Unit) -> Unit = { onClose ->
            UnreadMark.entries.forEachIndexed { index, mark ->
                ObDropdownMenuItem(
                    text = mark.value, trailingIcon = mark.trailingIconRes,
                    leadingIcon = if (unreadMark == mark) R.drawable.check else null,
                    onClick = {
                        Env.settings.unreadMark.value = mark.value
                        onClose()
                    }
                )
                if (index < UnreadMark.entries.lastIndex) {
                    DropdownMenuDivider()
                }
            }
        }

        val openContentMenus: @Composable (onClose: () -> Unit) -> Unit = { onClose ->
            OpenContentWith.entries.forEachIndexed { index, with ->
                ObDropdownMenuItem(
                    text = with.value,
                    leadingIcon = if (openContentWith == with) R.drawable.check else null,
                    onClick = {
                        Env.settings.openContentWith.value = with.value
                        onClose()
                    }
                )
                if (index < OpenContentWith.entries.lastIndex) {
                    DropdownMenuDivider()
                }
            }
        }

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
                            menuContent = unreadMarkMenus
                        )
                        SpacerDivider(start = 52.dp, end = 12.dp)
                        ListTileChevronUpDown(
                            title = "默认打开方式", icon = R.drawable.page,
                            trailing = openContentWith.value,
                            menuContent = openContentMenus
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
                            onClick = {
                                showFolderPicker = true
                            }
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


