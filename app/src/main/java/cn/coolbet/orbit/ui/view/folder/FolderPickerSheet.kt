package cn.coolbet.orbit.ui.view.folder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.ui.kit.SheetTopBar

@Composable
fun FolderPickerSheet(
    folders: List<Folder> = emptyList(),
    selectedValue: Long = 0,
    onValueChange: (Long) -> Unit = {},
    onBack: () -> Unit = {},
){
    Column {
        SheetTopBar(title = "Add to Folder", onBack = onBack)
        FolderPicker(
            folders = folders,
            selectedValue = selectedValue,
            onValueChange = onValueChange
        )
        Spacer(modifier = Modifier.height(21.dp))
    }
}