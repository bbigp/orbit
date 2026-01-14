package cn.coolbet.orbit.model.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import cn.coolbet.orbit.model.domain.OpenContentWith
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity(tableName = "ld_settings")
@Parcelize
data class LDSettings(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo("user_id") val userId: Long = 0,
    @ColumnInfo("meta_id") val metaId: String = "",
    @ColumnInfo("display_mode") val displayMode: DisplayMode = DisplayMode.Magazine,
    @ColumnInfo("sort_order") val sortOrder: LDSort = LDSort.PublishedAt,  //sortDirection
    @ColumnInfo("unread_only") val unreadOnly: Boolean = false,
    @ColumnInfo("show_group_title") val showGroupTitle: Boolean = false,
    @ColumnInfo("hide_globally") val hideGlobally: Boolean = false, //todo:  这个可能不对
    @ColumnInfo("auto_reader_view") val autoReaderView: Boolean = false,
    @ColumnInfo(name = "created_at", defaultValue = "0") val createdAt: Long = Date().time,
    @ColumnInfo(name = "changed_at", defaultValue = "0") val changedAt: Long = Date().time,
    @ColumnInfo(name = "open_content_with") val openContentWith: OpenContentWith = OpenContentWith.Default
) : Parcelable {

    companion object {
        val defaultSettings = LDSettings()
    }
}

enum class LDSettingKey {
    UnreadOnly,
    ShowGroupTitle,
    SortOrder,
    DisPlayMode,
    AutoReaderView,
    OpenContentWith
}

class LDSettingsConverters {
    @TypeConverter
    fun fromDisplayMode(value: DisplayMode): String = value.value

    @TypeConverter
    fun toDisplayMode(value: String): DisplayMode = DisplayMode.fromValue(value)

    @TypeConverter
    fun fromLDSort(value: LDSort): String = value.value

    @TypeConverter
    fun toLDSort(value: String): LDSort = LDSort.fromValue(value)

    @TypeConverter
    fun fromOpenContentWith(value: OpenContentWith): String = value.value

    @TypeConverter
    fun toOpenContentWith(value: String): OpenContentWith = OpenContentWith.fromValue(value)
}

enum class DisplayMode(val value: String) {
    Magazine("magazine"),
    TextOnly("text-only"),
    Thread("thread"),
    Card("card");

    companion object {
        fun fromValue(value: String): DisplayMode {
            return entries.find { it.value == value } ?: Magazine
        }
    }
}
enum class LDSort(val value: String) {
    PublishedAt("published_at"),
    CreatedAt("created_at");

    companion object {
        fun fromValue(value: String): LDSort {
            return LDSort.entries.find { it.value == value } ?: PublishedAt
        }
    }
}