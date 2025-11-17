package cn.coolbet.orbit.ui.view.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.R
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

object ProfileScreen: Screen {
    private fun readResolve(): Any = ProfileScreen

    @Composable
    override fun Content() {
        val model = getScreenModel<ProfileScreenModel>()
        val state by model.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val unreadMarkMenus: @Composable () -> Unit = {
            ObDropdownMenuItem(text = "None", trailingIcon = R.drawable.ban)
            ObDropdownMenuItem(text = "Dot", trailingIcon = R.drawable.dot_m)
            ObDropdownMenuItem(text = "Number", trailingIcon = R.drawable.notification_num, leadingIcon = R.drawable.check)
        }

        SystemBarStyleModern(statusBarColor = ObTheme.colors.secondaryContainer, isLightStatusBars = false)
        Scaffold (
            containerColor = ObTheme.colors.secondaryContainer,
            topBar = {
                ObBackTopAppBar(
                    background = ObTheme.colors.secondaryContainer,
                    title = "设置",
                    navigator = navigator
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
                            trailing = "内置阅读器"
                        )
                        SpacerDivider(start = 52.dp, end = 12.dp)
                        ListTileSwitch(
                            title = "自动已读", icon = R.drawable.check_o,
                            checked = state.user.autoRead
                        )
                        SpacerDivider(start = 52.dp, end = 12.dp)
                        ListTileChevronUpDown(
                            title = "根文件夹", icon = R.drawable.folder_1,
                            trailing = state.rootFolder.title
                        )
                    }
                }
                item {
                    ObCard {
                        SyncSubscriptions("01:01")
                    }
                }
                item {  Spacer(modifier = Modifier.height(24.dp)) }
                item {
                    ObCard {
                        SyncSubscriptions("01:01")
                    }
                }
                item {  Spacer(modifier = Modifier.height(24.dp)) }
                item {
                    ObCard {
                        SyncSubscriptions("01:01")
                    }
                }
                item {  Spacer(modifier = Modifier.height(24.dp)) }


                item { Spacer(modifier = Modifier.height(8.dp)) }
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
                            trailing = "内置阅读器"
                        )
                        SpacerDivider(start = 52.dp, end = 12.dp)
                        ListTileSwitch(
                            title = "自动已读", icon = R.drawable.check_o,
                            checked = state.user.autoRead
                        )
                        SpacerDivider(start = 52.dp, end = 12.dp)
                        ListTileChevronUpDown(
                            title = "根文件夹", icon = R.drawable.folder_1,
                            trailing = state.rootFolder.title
                        )
                    }
                }
                item {  Spacer(modifier = Modifier.height(24.dp)) }
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


