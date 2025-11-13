package cn.coolbet.orbit.ui.view.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.R
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.ui.kit.ObIcon
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black75


@Composable
fun SyncSubscriptions(time: String) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp).height(49.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text("Sync Subscriptions", maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R15)
            Text("Last Synced $time", modifier = Modifier.padding(start = 1.dp), maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R11B50)
        }
        ObIcon(id = R.drawable.check_o, color = Black75, onClick = {
            NavigatorBus.push(Route.Sync)
        })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSyncSubscriptions() {

    SyncSubscriptions("17:03")
}