package cn.coolbet.orbit.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.coolbet.orbit.model.entity.LDSettings

@Dao
abstract class LDSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(settings: LDSettings)

    @Query("""
        select * from ld_settings where meta_id = :metaId
    """)
    abstract suspend fun get(metaId: String): LDSettings?
}