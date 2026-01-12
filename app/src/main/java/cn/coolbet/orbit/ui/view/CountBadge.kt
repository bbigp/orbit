package cn.coolbet.orbit.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.toBadgeText
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.asUnreadMarkState
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.domain.UnreadMark
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.home.LocalUnreadState


@Composable
fun CountBadge(metaId: MetaId) {
    val unreadMark by Env.settings.unreadMark.asUnreadMarkState()
    if (unreadMark == UnreadMark.NONE) {
        return
    }

    val unreadState = LocalUnreadState.current
    val unreadMap by unreadState
    val count = unreadMap[metaId.toString()] ?: 0
    if (count == 0) return

    if (unreadMark == UnreadMark.DOT) {
        return Box(modifier = Modifier.padding(all = 7.5.dp),
                contentAlignment = Alignment.Center,
            ) {
            Image(
                modifier = Modifier.size(5.dp),
                painter = painterResource(id = R.drawable.badge_dot),
                contentDescription = "",
                contentScale = ContentScale.Fit,
            )
        }
    }
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            text = count.toBadgeText,
            style = AppTypography.M13B25,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

