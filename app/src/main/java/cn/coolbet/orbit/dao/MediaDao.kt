package cn.coolbet.orbit.dao

import androidx.room.Dao
import androidx.room.Query
import cn.coolbet.orbit.model.domain.Media
import cn.coolbet.orbit.model.entity.MediaEntity
import cn.coolbet.orbit.model.entity.to

@Dao
abstract class MediaDao {

    suspend fun getMap(entryIds: List<Long>): Map<Long, List<Media>> {
        if (entryIds.isEmpty()) return emptyMap()
        val mediaEntities = getMediasImpl(entryIds).map { it.to() }
        return mediaEntities.groupBy { it.entryId }
    }

    @Query("delete from medias")
    abstract suspend fun clearAll()


    @Query("select * from medias where entry_id in (:entryIds)")
    internal abstract suspend fun getMediasImpl(entryIds: List<Long>): List<MediaEntity>

}