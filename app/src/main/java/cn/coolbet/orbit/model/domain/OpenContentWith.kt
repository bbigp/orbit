package cn.coolbet.orbit.model.domain

import androidx.compose.runtime.Composable
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.kit.DropdownMenuDivider
import cn.coolbet.orbit.ui.kit.ObDropdownMenuItem
import com.google.gson.annotations.SerializedName

enum class OpenContentWith(val value: String) {
    @SerializedName("Default") Default("默认"),
    @SerializedName("ReaderView") READER_VIEW("内置阅读器"),
    @SerializedName("InAppBrowser") IN_APP_BROWSER("应用内浏览器"),
    @SerializedName("SystemBrowser") SYSTEM_BROWSER("系统浏览器");
    companion object {
        fun fromValue(value: String?): OpenContentWith =
            OpenContentWith.entries.find { it.value == value } ?: READER_VIEW
    }
}

@Composable
fun OpenContentWith.Companion.GenerateMenuItems(
    selectedValue: OpenContentWith,
    filterValue: OpenContentWith? = null,
    onClick: (OpenContentWith) -> Unit,
) {
    val filteredEntries = OpenContentWith.entries
        .filter { filterValue != null && it != filterValue }

    filteredEntries.forEachIndexed { index, item ->
        ObDropdownMenuItem(
            text = item.value,
            leadingIcon = if (selectedValue == item) R.drawable.check else null,
            onClick = { onClick(item) }
        )

        // 注意：这里的分割线判断逻辑使用了过滤后的列表长度
        if (index < filteredEntries.lastIndex) {
            DropdownMenuDivider()
        }
    }
}