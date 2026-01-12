package cn.coolbet.orbit.ui.kit

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black04
import cn.coolbet.orbit.ui.theme.ObTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewObTopAppbar(){
    Column {
        ObBackTopAppBar(
            title = {
                Row(
                    modifier = Modifier
                ) {
                    Text(
                        "state.meta.title",
                        maxLines = 1,
                        style = AppTypography.M17,
                    )
                    Box(
                        modifier = Modifier.padding(start = 8.dp)
//                                    .clip(RoundedCornerShape(20.dp))
                            .background(Black04, shape = RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            "99+",
                            maxLines = 1,
                            style = AppTypography.M13B25,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 1.dp)
                        )
                    }
                }
            },
        )
        Row {
            Text("标题", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
        TopAppBar(title = {})

        ObTopAppbar(
            navigationIcon = {
                ObIcon(id = R.drawable.lines_3)
            },
            actions = {
                ObIconGroup {
                    ObIcon(id = R.drawable.add)
                    ObIcon(id = R.drawable.page)
                }
                Box(modifier = Modifier.height(28.dp).width(28.dp).background(Color.Cyan))
            }
        )

        ObTextFieldAppbar(icon = R.drawable.search, button = {
            ObTextButton(
                "取消",
                colors = OButtonDefaults.ghost,
                sizes = OButtonDefaults.mediumPadded.copy(horizontalPadding = 0.1.dp),
            )
        }, keyboardActions = KeyboardActions {  })
        ObTextFieldAppbar(icon = R.drawable.search, keyboardActions = KeyboardActions {  })
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObTopAppbar(
    title: @Composable () -> Unit = {},
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
            navigationIcon()
            Spacer(modifier = Modifier.width(16.dp))
            title()
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd,
            ) { Row { actions() } }
        }
//        if (title != "") {
//            Text(
//                text = title, style = AppTypography.M17,
//                modifier = Modifier.align(Alignment.Center).fillMaxWidth()
//                    .padding(horizontal = ((LocalConfiguration.current.screenWidthDp + 24) / 3).dp),
//                textAlign = TextAlign.Center,
//                maxLines = 1, overflow = TextOverflow.Ellipsis,
//            )
//        }
    }
}


@Composable
fun ObBackTopAppBar(
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    background: Color = ObTheme.colors.primaryContainer,
) {
    val navigator = LocalNavigator.current
    ObTopAppbar(
        title = title,
        actions = actions,
        navigationIcon = {
            ObIcon(
                id = R.drawable.arrow_left,
                modifier = Modifier.background(background)
                    .clickable(onClick = { navigator?.pop() })
            )
        },
        background = background,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObTextFieldAppbar(
    icon: Int,
    keyboardActions: KeyboardActions,
    button: (@Composable RowScope.() -> Unit)? = null,
    background: Color = ObTheme.colors.primaryContainer,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    focusRequester: FocusRequester? = null,
){
    Box(
        // 让 Box 自身处理 Insets，将内容向下推
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(TopAppBarDefaults.windowInsets) // 处理状态栏 Insets
            .height(52.dp) // 最终高度 = 状态栏 Insets + 50.dp
            .background(background), // 设置背景色
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 12.dp)
                .height(36.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                ObIconTextField(
                    value = value,
                    icon = icon,
                    onValueChange = onValueChange,
                    focusRequester = focusRequester,
                    keyboardActions = keyboardActions
                )
            }
            if (button != null) {
                Spacer(modifier = Modifier.width(16.dp))
                button()
            }
        }
    }
}