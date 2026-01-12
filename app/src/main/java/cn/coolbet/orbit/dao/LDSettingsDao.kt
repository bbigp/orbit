package cn.coolbet.orbit.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.entity.DisplayMode
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.model.entity.LDSort

@Dao
abstract class LDSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(settings: LDSettings)

    @Query("""
        select * from ld_settings where meta_id = :metaId
    """)
    abstract suspend fun get(metaId: String): LDSettings?

    @Transaction
    open suspend fun update(
        metaId: MetaId,
        displayMode: DisplayMode? = null,
        unreadOnly: Boolean? = null,
        sortOrder: LDSort? = null,
        showGroupTitle: Boolean? = null,
        autoReaderView: Boolean? = null
    ): LDSettings {
        val existing = get(metaId.toString())
        if (existing != null) {
            val updated = existing.copy(
                metaId = metaId.toString(),
                displayMode = displayMode ?: existing.displayMode,
                unreadOnly = unreadOnly ?: existing.unreadOnly,
                sortOrder = sortOrder ?: existing.sortOrder,
                showGroupTitle = showGroupTitle ?: existing.showGroupTitle,
                autoReaderView = autoReaderView ?: existing.autoReaderView
            )
            updateImpl(updated)
            return updated
        }
        val newSettings = LDSettings.defaultSettings.copy(
            metaId = metaId.toString(),
            displayMode = displayMode ?: DisplayMode.Magazine,
            unreadOnly = unreadOnly ?: false,
            sortOrder = sortOrder ?: LDSort.PublishedAt,
            showGroupTitle = showGroupTitle ?: false,
            autoReaderView = autoReaderView ?: false
        )
        insert(newSettings)
        return newSettings
    }

    @Update
    internal abstract suspend fun updateImpl(settings: LDSettings)
}