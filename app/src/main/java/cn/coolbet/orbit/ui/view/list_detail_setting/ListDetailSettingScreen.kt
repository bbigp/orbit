package cn.coolbet.orbit.ui.view.list_detail_setting

import android.os.Parcelable
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListDetailSettingScreen(
  val feedId: Long
): Screen, Parcelable {

    @Composable
    override fun Content() {
    }
}