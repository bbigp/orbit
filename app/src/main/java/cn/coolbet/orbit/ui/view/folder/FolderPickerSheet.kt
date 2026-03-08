package cn.coolbet.orbit.ui.view.folder

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.ui.kit.DragHandle
import cn.coolbet.orbit.ui.kit.SheetTopBar
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
data class FolderPickerSheet(
    val folders: List<Folder> = emptyList(),
    val selectedId: Long,
    @IgnoredOnParcel
    val onValueChange: (Long) -> Unit = {},
): Screen, Parcelable {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Column {
            DragHandle()
            SheetTopBar(title = "Add to Folder", onBack = { navigator.pop() })
            FolderPicker(
                folders = folders,
                selectedValue = selectedId,
                onValueChange = onValueChange
            )
            Spacer(modifier = Modifier.height(21.dp))
        }
    }
}
