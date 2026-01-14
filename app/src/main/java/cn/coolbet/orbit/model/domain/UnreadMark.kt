package cn.coolbet.orbit.model.domain

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.kit.DropdownMenuDivider
import cn.coolbet.orbit.ui.kit.ObDropdownMenuItem
import com.google.gson.annotations.SerializedName

enum class UnreadMark(val value: String, @DrawableRes val trailingIconRes: Int) {
    @SerializedName("None") NONE("None", R.drawable.ban),
    @SerializedName("Dot") DOT("Dot", R.drawable.dot_m),
    @SerializedName("Number") NUMBER("Number", R.drawable.notification_num),
    ;
    companion object {
        fun fromValue(value: String?): UnreadMark =
            UnreadMark.entries.find { it.value == value } ?: NUMBER
    }
}

@Composable
fun UnreadMark.Companion.GenerateMenuItems(
    selectedValue: UnreadMark,
    onClick: (UnreadMark) -> Unit,
) {
    UnreadMark.entries.forEachIndexed { index, mark ->
        ObDropdownMenuItem(
            text = mark.value, trailingIcon = mark.trailingIconRes,
            leadingIcon = if (selectedValue == mark) R.drawable.check else null,
            onClick = { onClick(mark) }
        )
        if (index < UnreadMark.entries.lastIndex) {
            DropdownMenuDivider()
        }
    }
}