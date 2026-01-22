package cn.coolbet.orbit.ui.view.listdetail.component.item

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.common.toBadgeText
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.asUnreadMarkState
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.UnreadMark
import cn.coolbet.orbit.ui.kit.DashedDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.home.LocalUnreadState


@Composable
fun LDHeader(
    meta: Meta,
    modifier: Modifier = Modifier
) {
    val unreadState = LocalUnreadState.current
    val unreadMark by Env.settings.unreadMark.asUnreadMarkState()
    val unreadMap by unreadState
    val count = unreadMap[meta.metaId.toString()] ?: 0
    Column(
        modifier = modifier.padding(horizontal = 14.dp)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            meta.title,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = AppTypography.M28
        ) //8 + 34 + 4 + 13 + 16 + 2
        if (unreadMark == UnreadMark.NUMBER && count > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${count.toBadgeText}未读",
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = AppTypography.M11B25
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        DashedDivider(indent = 2.dp)
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun PreviewEntriesTopTile() {
    val meta = Feed.EMPTY.copy(title = "少数派 - sspai")
    val unreadMap = mapOf(
        "e1" to 42
    )
    CompositionLocalProvider(
        LocalUnreadState provides mutableStateOf(unreadMap)
    ) {
        Column {
            LDHeader(meta)
            LDHeader(meta.copy(id = 1))
        }
    }
}