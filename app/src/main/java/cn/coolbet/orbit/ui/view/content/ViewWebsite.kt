package cn.coolbet.orbit.ui.view.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.openURL
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObIconTextButton

@Composable
fun ViewWebsite(url: String) {
    if (url.isEmpty()) return
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        ObIconTextButton(
            content = "View Website",
            icon = R.drawable.out_o,
            sizes = OButtonDefaults.mediumPadded.copy(horizontalPadding = 32.dp),
            colors = OButtonDefaults.secondary,
            iconOnRight = true,
            onClick = { openURL(context, url.toUri()) }
        )
    }
}