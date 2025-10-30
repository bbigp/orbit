package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.R
import cn.coolbet.orbit.view.profile.ProfileScreen


@Preview(showBackground = true)
@Composable
fun PreviewM3CustomTopBar(){
    M3CustomTopBar()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M3CustomTopBar(
) {
    val navigator = LocalNavigator.currentOrThrow
    Box(
        // 让 Box 自身处理 Insets，将内容向下推
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(TopAppBarDefaults.windowInsets) // 处理状态栏 Insets
            .height(50.dp) // 最终高度 = 状态栏 Insets + 50.dp
            .background(Color.White) // 设置背景色
    ) {
        // 🌟 2. 使用 TopAppBar (或 Row) 作为内容组织者
        TopAppBar(
            // 🚨 移除所有高度和 Insets 相关的 Modifier，让它填充这个 50.dp 的 Box
            modifier = Modifier.fillMaxSize(),

            // 关键：将 TopAppBar 的背景色设为透明，避免它再画一次背景
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),

            // 关键：移除其内部的 Insets (否则会双重计算)
            windowInsets = WindowInsets(0.dp),

            title = { /* 留空 */ },
            navigationIcon = {
                IconButton(onClick = {
                    navigator.push(ProfileScreen)
                }) { ObIcon(R.drawable.lines_3) }
            },
            actions = {
                IconButton(onClick = { /* ... */ }) { ObIcon(R.drawable.add) }
            }
        )
    }
}