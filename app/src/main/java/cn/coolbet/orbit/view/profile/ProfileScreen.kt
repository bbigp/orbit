package cn.coolbet.orbit.view.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.kit.M3CustomTopBar
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.PreviewListTileChevronUpDown

object ProfileScreen: Screen {
    private fun readResolve(): Any = ProfileScreen

    @Composable
    override fun Content() {

        Scaffold (
            topBar = { M3CustomTopBar() }
        ) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                Column {
                    PreviewListTileChevronUpDown()

                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewListItem() {
    ListItem(
        // 必填：主标题
        headlineContent = { Text("Compose ListItem") },


        // 可选：左侧内容
        leadingContent = {
            ObIcon(R.drawable.lines_3)
        },

        // 可选：右侧内容
        trailingContent = {
            Text("详情")
        },

        modifier = Modifier
    )
}

