package cn.coolbet.orbit.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Transaction
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.domain.Media
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

@Dao
abstract class EntryDao(private val db: AppDatabase) {

    @Transaction
    suspend fun batchSave(items: List<Entry>) = withContext(Dispatchers.IO) {
        if (items.isEmpty()) return@withContext
        val sql = """
            insert into entries(
                id, user_id, hash, feed_id, status,
                title, url, published_at, content, author,
                starred, reading_time, tags, created_at, changed_at
            ) VALUES (
                ?, ?, ?, ?, ?,      ?, ?, ?, ?, ?,      ?, ?, ?, ?, ?
            ) ON CONFLICT(id) DO nothing
        """
        val medias = mutableListOf<Media>()
        for (item in items) {
            medias.addAll(item.medias)
            val args = arrayOf(
                item.id, item.userId, item.hash, item.feed, item.status.value,
                item.title, item.url, item.publishedAt, item.content, item.author,
                item.starred, item.readingTime, item.tags.joinToString(","), item.createdAt, item.changedAt
            )
            db.openHelper.writableDatabase.execSQL(sql, args)
        }

        if (medias.isEmpty()) return@withContext
        val mediaSql = """
            insert into medias(
                id, user_id, entry_id, url, mime_type, size
            ) values (
                ?, ?, ?, ?, ?,       ?
            ) ON CONFLICT(id) DO nothing
        """
        for (item in medias) {
            val args = arrayOf<Any>(item.id, item.userId, item.entryId, item.url, item.mimeType, item.size)
            db.openHelper.writableDatabase.execSQL(mediaSql, args)
        }

    }

}