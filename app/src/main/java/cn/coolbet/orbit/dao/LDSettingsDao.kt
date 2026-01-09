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

@Dao
abstract class LDSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(settings: LDSettings)

    @Query("""
        select * from ld_settings where meta_id = :metaId
    """)
    abstract suspend fun get(metaId: String): LDSettings?

    @Transaction
    suspend fun update(metaId: MetaId, displayMode: DisplayMode? = null) {
        val existing = get(metaId.toString())
        if (existing != null) {
            val updated = existing.copy(
                displayMode = displayMode ?: existing.displayMode,
            )
            updateImpl(updated)
            return
        }
        val newSettings = LDSettings.defaultSettings.copy(
            displayMode = displayMode ?: DisplayMode.Magazine
        )
        insert(newSettings)
    }

    @Update
    internal abstract suspend fun updateImpl(settings: LDSettings)
}