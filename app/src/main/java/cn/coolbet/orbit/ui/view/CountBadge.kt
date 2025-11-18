package cn.coolbet.orbit.ui.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.UnreadMark
import cn.coolbet.orbit.model.domain.User
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.home.LocalUnreadState
import cn.coolbet.orbit.ui.view.home.LocalUserState


@Composable
fun CountBadge(metaId: String) {
    val userState = LocalUserState.current
    val user by userState
    if (user.unreadMark == UnreadMark.NONE) {
        return
    }

    val unreadState = LocalUnreadState.current
    val unreadMap by unreadState
    val count = unreadMap[metaId] ?: 0
    if (count == 0) return

    if (user.unreadMark == UnreadMark.DOT) {
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
            text = "$count",
            style = AppTypography.M13B25,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}


@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun PreviewCountBadge(){
    val sampleUnreadState = mutableStateOf<Map<String,Int>>(mapOf(
        "1" to 5
    ))
    val dotUserState = mutableStateOf(User.EMPTY.copy(unreadMark = UnreadMark.DOT))
    val noneUserState = mutableStateOf(User.EMPTY.copy(unreadMark = UnreadMark.NONE))
    Column {
        CompositionLocalProvider(
            LocalUnreadState provides sampleUnreadState
        ) {
            CountBadge(metaId = "1")
        }
        Spacer(modifier = Modifier.height(10.dp).width(20.dp).background(Color.Red))
        CompositionLocalProvider(
            LocalUnreadState provides sampleUnreadState,
                LocalUserState provides dotUserState,
        ) {
            CountBadge(metaId = "1")
        }
        Spacer(modifier = Modifier.height(10.dp).width(20.dp).background(Color.Red))
        CompositionLocalProvider(
            LocalUnreadState provides sampleUnreadState,
            LocalUserState provides noneUserState,
        ) {
            CountBadge(metaId = "1")
        }
    }
}

