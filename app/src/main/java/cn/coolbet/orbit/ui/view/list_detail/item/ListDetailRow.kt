package cn.coolbet.orbit.ui.view.list_detail.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.coolbet.orbit.NavigatorBus
import cn.coolbet.orbit.Route
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.ui.view.content.QueryContext


@Composable
fun ListDetailRow(
    item: Entry
){

    LDMagazine(
        item,
        modifier = Modifier.click {
            NavigatorBus.push(
                Route.Entry(
                    entry = item,
                    queryContext = QueryContext.normal
                )
            )
        }
    )
}