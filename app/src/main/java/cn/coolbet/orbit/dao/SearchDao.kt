package cn.coolbet.orbit.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.entity.SearchRecord

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: SearchRecord): Long

    @Query("select distinct word from search_records where meta_id = :metaId order by id desc limit 15")
    suspend fun getList(metaId: String): List<String>

    @Query("delete from search_records where meta_id = :metaId")
    suspend fun deleteAll(metaId: String)
}