package cn.coolbet.orbit.ui.view.list_detail.setting_sheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.common.copyText
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.GenerateMenuItems
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.OpenContentWith
import cn.coolbet.orbit.model.entity.LDSettingKey
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.kit.ListTileChevronUpDown
import cn.coolbet.orbit.ui.kit.ListTileSwitch
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.ObToastManager
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.kit.ToastType
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black04
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.Black25
import cn.coolbet.orbit.ui.theme.ContainerSecondary
import cn.coolbet.orbit.ui.theme.ObTheme
import cn.coolbet.orbit.ui.view.FeedIcon
import cn.coolbet.orbit.ui.view.FeedIconDefaults
import cn.coolbet.orbit.ui.view.list_detail.LocalChangeLDSettings

enum class SheetPage {
    ListDetailSetting, FeedSetting
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailSettingSheet(
    meta: Meta,
    settings: LDSettings,
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
){
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentPage by remember { mutableStateOf(SheetPage.ListDetailSetting) }
    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = RoundedCornerShape(16.dp),
            containerColor = ObTheme.colors.secondaryContainer,
            dragHandle = {
                DragHandle()
            },
        ) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState == SheetPage.FeedSetting) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(slideOutHorizontally { width -> width } + fadeOut())
                    }
                },
                label = "SheetContentAnimation"
            ) { targetPage ->
                when(targetPage) {
                    SheetPage.ListDetailSetting -> {
                        ListDetailSettingSheetContent(
                            meta, settings,
                            onNavigateToFeedSetting = { currentPage = SheetPage.FeedSetting }
                        )
                    }
                    else -> { FeedSettingSheetContent(feed = meta as Feed, onBack = { currentPage = SheetPage.ListDetailSetting }) }
                }
            }
        }
    }

}

@Composable
fun ListDetailSettingSheetContent(
    meta: Meta,
    settings: LDSettings,
    onNavigateToFeedSetting: () -> Unit
) {
    val changeLDSettings = LocalChangeLDSettings.current
    Column(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 21.dp)
            .fillMaxWidth()
    ) {
        if (meta is Feed) {
            EditFeedView(meta, onNavigateToFeedSetting)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text("View", maxLines = 1, style = AppTypography.M15B25,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        DisplayModePicker(
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


@Preview(showBackground = true)
@Composable
fun PreviewListCard() {
    val meta = Feed.EMPTY.copy(title = "少数派 - sspai", feedURL = "htts://sspai.com/feed")
    Column {
        EditFeedView(meta)
    }
}


@Composable
fun EditFeedView(
    meta: Meta,
    onNavigateToFeedSetting: () -> Unit = {}
) {
    val context = LocalContext.current
    Column (
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = Black08, shape = RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            FeedIcon(url = meta.iconURL, alt = meta.title, size = FeedIconDefaults.LARGE)

            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(meta.title, style = AppTypography.M15, maxLines = 1)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(meta.url, style = AppTypography.R13B50, maxLines = 1)
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        modifier = Modifier.size(16.dp)
                            .click {
                                copyText(context, meta.url)
                                ObToastManager.show("Link copied", type = ToastType.Success)
                            },
                        painter = painterResource(id = R.drawable.copy),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Black25)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Box(
                modifier = Modifier.height(48.dp).width(60.dp)
                    .background(Black04, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .click(onClick = onNavigateToFeedSetting),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                )
            }


            Spacer(modifier = Modifier.width(6.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}