package cn.coolbet.orbit.ui.view.addfeed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.common.click
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObAsyncTextButton
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.view.listdetail.component.LDItemListState

@Preview(showBackground = true)
@Composable
fun PreviewAddFeedCollapsedActions() {
    androidx.compose.foundation.layout.Column {
        AddFeedCollapsedHeader("sspai.com", onExpand = {})
        AddFeedActionBar(onCancel = {}, onAdd = {})
    }
}

@Composable
internal fun AddFeedCollapsedHeader(
    title: String,
    onExpand: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
            .click(onClick = onExpand)
    ) {
        Text(
            title,
            maxLines = 1,
            style = AppTypography.M17,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun AddFeedActionBar(
    onCancel: () -> Unit,
    onAdd: () -> Unit,
) {
    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)) {
        Row {
            Box(modifier = Modifier.weight(1f)) {
                ObAsyncTextButton(
                    "Cancel",
                    sizes = OButtonDefaults.large,
                    colors = OButtonDefaults.danger,
                    onClick = onCancel
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                ObAsyncTextButton(
                    "Add",
                    sizes = OButtonDefaults.large,
                    onClick = onAdd
                )
            }
        }
    }
}

internal data class AddFeedPreviewListState(
    override val meta: Meta,
    override val settings: LDSettings = LDSettings.defaultSettings,
    override val isRefreshing: Boolean = false,
    override val hasMore: Boolean = false,
) : LDItemListState

