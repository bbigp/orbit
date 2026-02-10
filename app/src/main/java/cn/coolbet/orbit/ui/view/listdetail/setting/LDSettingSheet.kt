package cn.coolbet.orbit.ui.view.listdetail.setting

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.entity.LDSettingKey
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.kit.ListTileSwitch
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.feed.EditFeedSheet
import cn.coolbet.orbit.ui.view.listdetail.LocalChangeLDSettings
import kotlinx.parcelize.Parcelize

@Parcelize
object LDSettingSheet: Screen, Parcelable {
    private fun readResolve(): Any = LDSettingSheet

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @Composable
    override fun Content() {
        val model = koinScreenModel<LDSettingScreenModel>()
        val state by model.coordinator.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            DragHandle()
            Spacer(modifier = Modifier.height(8.dp))

            if (state.meta is Feed) {
                FeedInfoCardView(state.meta, onNavigateToEditFeed = { navigator.push(EditFeedSheet(state.meta as Feed)) })
                Spacer(modifier = Modifier.height(12.dp))
            }
            Text("View", maxLines = 1, style = AppTypography.M15B25,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            CompositionLocalProvider(
                LocalChangeLDSettings provides model::changeLDSettings
            ) {
                DisplayModePickerView(
                    metaId = state.meta.metaId,
                    displayMode = state.settings.displayMode
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

//                Text("Sort by", maxLines = 1, style = AppTypography.M15B25,
//                    modifier = Modifier.padding(horizontal = 20.dp)
//                )
//                Spacer(modifier = Modifier.height(6.dp))
            //排序
//                Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                ObCard {
                    ListTileSwitch(
                        title = "Unread Only", icon = R.drawable.eyes,
                        checked = state.settings.unreadOnly,
                        onCheckedChange = { v->
                            model.changeLDSettings(state.meta.metaId, LDSettingKey.UnreadOnly, v)
                        }
                    )
                    ListTileSwitch(
                        title = "Automatic Reader View", icon = R.drawable.book,
                        checked = state.settings.autoReaderView,
                        onCheckedChange = { v->
                            model.changeLDSettings(state.meta.metaId, LDSettingKey.AutoReaderView, v)
                        }
                    )
                    SpacerDivider(start = 52.dp, end = 12.dp)
                    ListTileSwitch(
                        title = "Group by Date", icon = R.drawable.list_label,
                        checked = state.settings.showGroupTitle,
                        onCheckedChange = { v->
                            model.changeLDSettings(state.meta.metaId, LDSettingKey.ShowGroupTitle, v)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(21.dp))
        }
    }

}