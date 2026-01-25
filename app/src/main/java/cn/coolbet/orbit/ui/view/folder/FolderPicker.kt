package cn.coolbet.orbit.ui.view.folder

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.kit.ObRadio
import cn.coolbet.orbit.ui.kit.ObRadioDefaults
import cn.coolbet.orbit.ui.theme.AppTypography


@Composable
fun FolderPicker(
    folders: List<Folder> = emptyList(),
    selectedValue: Long = 0,
    onValueChange: (Long) -> Unit = {}
) {
    ObCard(
        horizontal = 16.dp,
        contentVertical = 8.dp
    ) {
        folders.forEach { folder ->
            FolderRadio(folder, selectedValue, onValueChange)
        }
    }
}


@Composable
fun FolderRadio(
    folder: Folder,
    selectedValue: Long = 0,
    onValueChange: (Long) -> Unit = {}
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = Modifier.height(40.dp).fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onValueChange(folder.id)
                haptics.performHapticFeedback(HapticFeedbackType.ToggleOn)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.group),
            contentDescription = "",
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            folder.title,
            modifier = Modifier.weight(1f),
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = AppTypography.R15
        )
        Spacer(modifier = Modifier.width(12.dp))
        ObRadio(
            selected = selectedValue == folder.id,
            sizes = ObRadioDefaults.medium,
            onClick = { onValueChange(folder.id) }
        )
        Spacer(modifier = Modifier.width(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFolderRadio() {
    Column {
        FolderRadio(Folder.EMPTY)
        FolderRadio(Folder.EMPTY, selectedValue = 1)
    }
}