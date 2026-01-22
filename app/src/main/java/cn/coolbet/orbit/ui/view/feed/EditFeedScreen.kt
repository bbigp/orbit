package cn.coolbet.orbit.ui.view.feed

import android.os.Parcelable
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cn.coolbet.orbit.model.domain.Feed
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditFeedScreen(
    val feed: Feed,
): Screen, Parcelable {

    @Composable
    override fun Content() {
        EditFeedView(feed, {})
    }

}