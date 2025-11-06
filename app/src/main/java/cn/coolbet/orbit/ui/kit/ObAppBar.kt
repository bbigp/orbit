package cn.coolbet.orbit.ui.kit

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.ObTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewObTopAppbar(){
    Column {
        ObBackTopAppBar()
        Row {
            Text("标题", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
        TopAppBar(title = {})

        ObTopAppbar(
            navigationIcon = {
                ObIcon(id = R.drawable.lines_3)
            },
            actions = {
                ObIconGroup(items = listOf(
                    ObIconItem(iconId = R.drawable.add),
                    ObIconItem(iconId = R.drawable.page)
                ))
                Box(modifier = Modifier.height(28.dp).width(28.dp).background(Color.Cyan))
            }
        )
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObTopAppbar(
    title: String = "",
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    background: Color = ObTheme.colors.primaryContainer,
) {
    Box(
        // 让 Box 自身处理 Insets，将内容向下推
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(TopAppBarDefaults.windowInsets) // 处理状态栏 Insets
            .height(50.dp) // 最终高度 = 状态栏 Insets + 50.dp
            .background(background), // 设置背景色
    ) {
        Row(
            modifier = Modifier.height(50.dp).fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f)) { navigationIcon() }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd,
            ) { Row { actions() } }
        }
        if (title != "") {
            Text(
                text = title, style = AppTypography.M17,
                modifier = Modifier.align(Alignment.Center).fillMaxWidth()
                    .padding(horizontal = ((LocalConfiguration.current.screenWidthDp + 24) / 3).dp),
                textAlign = TextAlign.Center,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


@Composable
fun ObBackTopAppBar(
    title: String = "",
    actions: @Composable RowScope.() -> Unit = {},
    background: Color = ObTheme.colors.primaryContainer,
    navigator: Navigator? = null
) {
    ObTopAppbar(
        title = title,
        actions = actions,
        navigationIcon = {
            ObIcon(id = R.drawable.arrow_left, onClick = { navigator?.pop() }, background = background)
        },
        background = background,
    )
}