package cn.coolbet.orbit.dao

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import cn.coolbet.orbit.model.OrderCreatedAt
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.domain.Media
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.UnreadCount
import cn.coolbet.orbit.model.entity.EntryEntity
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import cn.coolbet.orbit.model.entity.to
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

@Dao
abstract class EntryDao(protected val db: AppDatabase) {

    suspend fun getEntries(page: Int, size: Int, meta: Meta, search: String = ""): List<Entry> {
        val whereClause = buildQuery(
            feedIds = meta.feedIds,
            statuses = meta.statuses,
            recentPubTime = meta.recentPubTime,
            recentAddTime = meta.recentAddTime,
            search = search,
        ).joinToString(separator = " and ")
        val orderByColumn = when (meta.order) {
            OrderCreatedAt -> "created_at"
            else -> "published_at"
        }
        val direction = "desc"
        val ordering = when (direction.lowercase()) {
            "asc" -> "asc"
            else -> "desc"
        }
        val offset = (page - 1) * size
        val query = """
            select * from entries where $whereClause
            order by $orderByColumn $ordering
            limit $size offset $offset
        """.trimIndent()
        Log.i("SQL", query)
        val sqliteQuery = SimpleSQLiteQuery(query)
        return this.getEntriesRaw(sqliteQuery).map { it.to() }
//        return getEntriesImpl().map { it.to() }
    }

    suspend fun batchSave(items: List<Entry>) = withContext(Dispatchers.IO) {
        if (items.isEmpty()) return@withContext
        val dbHelper = db.openHelper.writableDatabase
        dbHelper.beginTransaction()
        try {
            val sql = """
                insert or ignore into entries(
                    id, user_id, hash, feed_id, status,
                    title, url, published_at, content, author,
                    starred, reading_time, tags, created_at, changed_at
                ) VALUES (
                    ?, ?, ?, ?, ?,      ?, ?, ?, ?, ?,      ?, ?, ?, ?, ?
                )
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
                insert or ignore into medias(
                    id, user_id, entry_id, url, mime_type, size
                ) values (
                    ?, ?, ?, ?, ?,       ?
                )
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
        select feed_id, sum(case when status = 'unread' then 1 else 0 end) as count 
        from entries
        group by feed_id
    """)
    abstract suspend fun countFeedUnread(): List<UnreadCount>

    @Query("""
        update entries set 
            readable_content = :readable, 
            lead_image_url = :leadImageURL,
            summary = :summary
        where id = :id
    """)
    abstract suspend fun updateReadingModeData(readable: String, leadImageURL: String, summary: String,
        id: Long
    )

    @Query("select * from entries limit 10")
    internal abstract suspend fun getEntriesImpl(): List<EntryEntity>

    @RawQuery
    internal abstract suspend fun getEntriesRaw(query: SupportSQLiteQuery): List<EntryEntity>

    internal fun buildQuery(
        feedIds: List<Long> = emptyList(),
        recentPubTime: Int = 0,
        recentAddTime: Int = 0,
        statuses: List<EntryStatus> = emptyList(),
        search: String = "",
    ): List<String> {
        val cond = mutableListOf<String>()
        val nowSeconds = System.currentTimeMillis()

        if (feedIds.isNotEmpty()) {
            val ids = feedIds.joinToString(separator = ", ")
            cond.add("feed_id in ($ids)")
        }

        if (recentPubTime > 0) {
            val time = nowSeconds - (recentPubTime * 60)
            cond.add("published_at >= $time")
        }

        if (recentAddTime > 0) {
            val time = nowSeconds - (recentAddTime * 60)
            cond.add("created_at >= $time")
        }

        if (statuses.isNotEmpty()) {
            val status = statuses.joinToString(separator = ",") { "'${it.value}'" }
            cond.add("status in ($status)")
        }

        if (search.isNotBlank()) {
            val keyword = "%$search%"
            cond.add("""
                (
                title like '$keyword' or content like '$keyword' 
                or summary like '$keyword' or readable_content like '$keyword' 
                )
            """)
        }
        if (cond.isEmpty()) {
            cond.add("true")
        }
        return cond
    }

}