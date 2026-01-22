package cn.coolbet.orbit.ui.view.listdetail.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.entity.LDSettingKey
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.kit.ListTileSwitch
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.listdetail.LocalChangeLDSettings


@Composable
fun LDSettingView(
    meta: Meta,
    settings: LDSettings,
    onNavigateToEditFeed: () -> Unit
) {
    val changeLDSettings = LocalChangeLDSettings.current
    Column(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 21.dp)
            .fillMaxWidth()
    ) {
        if (meta is Feed) {
            EditFeedCardView(meta, onNavigateToEditFeed)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text("View", maxLines = 1, style = AppTypography.M15B25,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        DisplayModePickerView(
            metaId = meta.metaId,
            displayMode = settings.displayMode
        )
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
                    checked = settings.unreadOnly,
                    onCheckedChange = { v->
                        changeLDSettings(meta.metaId, LDSettingKey.UnreadOnly, v)
                    }
                )
                ListTileSwitch(
                    title = "Automatic Reader View", icon = R.drawable.book,
                    checked = settings.autoReaderView,
                    onCheckedChange = { v->
                        changeLDSettings(meta.metaId, LDSettingKey.AutoReaderView, v)
                    }
                )
                SpacerDivider(start = 52.dp, end = 12.dp)
                ListTileSwitch(
                    title = "Group by Date", icon = R.drawable.list_label,
                    checked = settings.showGroupTitle,
                    onCheckedChange = { v->
                        changeLDSettings(meta.metaId, LDSettingKey.ShowGroupTitle, v)
                    }
                )
            }
        }
    }
}