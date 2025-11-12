package cn.coolbet.orbit.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.domain.Media
import cn.coolbet.orbit.model.domain.UnreadCount
import cn.coolbet.orbit.model.entity.EntryEntity
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import cn.coolbet.orbit.model.entity.to
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

@Dao
abstract class EntryDao(protected val db: AppDatabase) {

    suspend fun getEntries(page: Int, size: Int): List<Entry> {
        return getEntriesImpl().map { it.to() }
    }

    suspend fun batchSave(items: List<Entry>) = withContext(Dispatchers.IO) {
        if (items.isEmpty()) return@withContext
        val dbHelper = db.openHelper.writableDatabase
        dbHelper.beginTransaction()
        try {
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
                val args = arrayOf<Any>(
                    item.id, item.userId, item.hash, item.feedId, item.status.value,
                    item.title, item.url, item.publishedAt, item.content, item.author,
                    item.starred, item.readingTime, item.tags.joinToString(","), item.createdAt, item.changedAt
                )
                dbHelper.execSQL(sql, args)
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
                dbHelper.execSQL(mediaSql, args)
            }
            dbHelper.setTransactionSuccessful()
        } finally {
            dbHelper.endTransaction()
        }
    }

    @Query("delete from entries")
    abstract suspend fun clearAll()

    @Query("""
        select feed_id, count(*) filter (where status = 'unread') as count 
        from entries
        group by feed_id
    """)
    abstract suspend fun countFeedUnread(): List<UnreadCount>

    @Query("select * from entries limit 10")
    internal abstract suspend fun getEntriesImpl(): List<EntryEntity>

}