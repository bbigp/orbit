package cn.coolbet.orbit.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
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

    suspend fun getEntries(page: Int, size: Int, meta: Meta): List<Entry> {

        val whereClause = buildQuery(
            feedIds = feedIds,
            statuses = statuses,
            publishedTime = publishedTime,
            addTime = addTime,
            search = search,
        ).joinToString(separator = " and ")
        val orderByColumn = when (order) {
            "createdAt" -> "created_at"
            else -> "published_at" // 默认按 published_at 排序
        }
        val ordering = when (direction.lowercase()) {
            "asc" -> OrderingMode.ASC.name
            else -> OrderingMode.DESC.name // 默认降序
        }
        val offset = (finalPage - 1) * finalSize
        val query = """
            select * from entries where $whereClause
            order by $orderByColumn $ordering
            limit $finalSize offset $offset
        """.trimIndent()
        println(query)
        val result = db.customSelect(query)
        return result.map { r -> mapToEntry(r) }.toList()
        return getEntriesImpl().map { it.to() }
    }

    private fun mapToEntry(data: Map<String, Any?>): Entry {
        // 实际应用中需要安全地转换和处理 null
        return Entry(
            id = data["id"] as Long,
            feedId = data["feed_id"] as Long,
            publishedAt = data["published_at"] as Long,
            createdAt = data["created_at"] as Long,
            status = data["status"] as String,
            title = data["title"] as String,
            content = data["content"] as String?,
            summary = data["summary"] as String?,
        )
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

    internal fun buildQuery(
        feedIds: List<Long> = emptyList(),
        publishedTime: Long = 0,
        addTime: Long = 0,
        statuses: List<EntryStatus> = emptyList(),
        search: String = "",
    ): List<String> {
        val cond = mutableListOf<String>()
        val nowSeconds = System.currentTimeMillis()

        if (feedIds.isNotEmpty()) {
            val ids = feedIds.joinToString(separator = ", ")
            cond.add("feed_id in ($ids)")
        }

        if (publishedTime > 0) {
            val time = nowSeconds - (publishedTime * 60)
            cond.add("published_at >= $time")
        }

        if (addTime > 0) {
            val time = nowSeconds - (addTime * 60)
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