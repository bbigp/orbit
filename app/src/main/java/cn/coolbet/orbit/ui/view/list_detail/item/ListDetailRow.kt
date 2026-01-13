package cn.coolbet.orbit.ui.view.list_detail.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.entity.DisplayMode


@Composable
fun ListDetailRow(
    item: Entry,
    displayMode: DisplayMode,
    modifier: Modifier = Modifier
){
    when(displayMode) {
        DisplayMode.Magazine -> LDMagazine(item, modifier = modifier)
        DisplayMode.TextOnly -> LDTextOnly(item, modifier = modifier)
        DisplayMode.Thread -> LDThread(item, modifier = modifier)
        DisplayMode.Card -> LDTextOnly(item, modifier = modifier)
    }

}