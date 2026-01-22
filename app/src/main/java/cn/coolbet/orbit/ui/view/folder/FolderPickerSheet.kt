package cn.coolbet.orbit.ui.view.folder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.ui.kit.SheetTopBar

@Composable
fun FolderPickerSheet(
    onBack: () -> Unit = {},
){
    Column {
        SheetTopBar(title = "Add to Folder", onBack = onBack)
//        FolderPicker(
//            folders = folders,
//            selectedValue = selectedValue,
//            onValueChange = onValueChange
//        )
        Spacer(modifier = Modifier.height(21.dp))
    }
}