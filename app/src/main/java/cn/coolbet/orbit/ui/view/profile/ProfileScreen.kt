package cn.coolbet.orbit.ui.view.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.OpenContentWith
import cn.coolbet.orbit.model.domain.UnreadMark
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.kit.DropdownMenuDivider
import cn.coolbet.orbit.ui.kit.ListTileChevronUpDown
import cn.coolbet.orbit.ui.kit.ListTileSwitch
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObBackTopAppBar
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.ObDropdownMenuItem
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.ObTextButton
import cn.coolbet.orbit.ui.kit.ObTopAppbar
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.kit.SystemBarStyleModern
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.ContainerSecondary
import cn.coolbet.orbit.ui.theme.ObTheme
import cn.coolbet.orbit.ui.view.folder.FolderPicker
import cn.coolbet.orbit.ui.view.folder.FolderPickerSheet
import cn.coolbet.orbit.ui.view.folder.PreviewFolderRadio
import kotlinx.coroutines.launch

object ProfileScreen: Screen {
    private fun readResolve(): Any = ProfileScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<ProfileScreenModel>()
        val state by model.state.collectAsState()
        val folders by model.folders.collectAsState()

        var showFolderPicker by remember { mutableStateOf(false) }
        FolderPickerSheet (
            show = showFolderPicker,
            onDismiss = {
                showFolderPicker = false
            },
            folders = folders,
            selectedValue = state.user.rootFolder,
            onValueChange = { id ->
                model.changeUser(rootFolderId = id)
            }
        )

        val unreadMarkMenus: @Composable (onClose: () -> Unit) -> Unit = { onClose ->
            UnreadMark.entries.forEachIndexed { index, mark ->
                ObDropdownMenuItem(
                    text = mark.value, trailingIcon = mark.trailingIconRes,
                    leadingIcon = if (state.user.unreadMark == mark) R.drawable.check else null,
                    onClick = {
                        model.changeUser(unreadMark = mark)
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
                    leadingIcon = if (state.user.openContent == with) R.drawable.check else null,
                    onClick = {
                        model.changeUser(openContent = with)
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
                    title = "设置",
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
                            trailing = state.user.unreadMark.value,
                            menuContent = unreadMarkMenus
                        )
                        SpacerDivider(start = 52.dp, end = 12.dp)
                        ListTileChevronUpDown(
                            title = "默认打开方式", icon = R.drawable.page,
                            trailing = state.user.openContent.value,
                            menuContent = openContentMenus
                        )
                        SpacerDivider(start = 52.dp, end = 12.dp)
                        ListTileSwitch(
                            title = "自动已读", icon = R.drawable.check_o,
                            checked = state.user.autoRead,
                            onCheckedChange = { v->
                                model.changeUser(autoRead = v)
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


