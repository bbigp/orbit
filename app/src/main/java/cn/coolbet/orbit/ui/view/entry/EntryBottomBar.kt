package cn.coolbet.orbit.ui.view.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.ObTheme

@Preview(showBackground = true)
@Composable
fun EntryBottomBar(
) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .height(49.dp)
                .background(ObTheme.colors.primaryContainer)
    ) {
        SpacerDivider(thickness = 1.dp)
        Row(
            modifier = Modifier.height(48.dp)
                .padding(start = 20.dp, end = 20.dp, bottom = 4.dp, top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            ObIcon(
                id = R.drawable.arrow_left,
                modifier = Modifier.clickable(onClick = { NavigatorBus.pop() }),
            )
            ObIcon(id = R.drawable.check_o)
            ObIcon(id = R.drawable.star)
            ObIcon(id = R.drawable.page)
            ObIcon(id = R.drawable.chevron_down)
            ObIcon(id = R.drawable.more)
        }
    }

}