package cn.coolbet.orbit.ui.view.content.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.kit.DashedDivider
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.ObTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDisplaySettingSheet(
    show: Boolean,
    onDismiss: () -> Unit,
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    LaunchedEffect(show) {
        if (show) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = RoundedCornerShape(16.dp),
            containerColor = ObTheme.colors.secondaryContainer,
            dragHandle = {
                DragHandle()
            },
        ) {
            Column {
                ContentDisplaySettingsSheetTitle()
                ContentBgColorSetting()
                Spacer(modifier = Modifier.height(16.dp))
                DashedDivider(indent = 20.dp)
                ContentFontSetting()
                Spacer(modifier = Modifier.height(16.dp))
                //Text Size
                //Line Height
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}