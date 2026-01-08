package cn.coolbet.orbit.dao

import androidx.room.Dao
import androidx.room.Query
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.entity.FeedEntity
import cn.coolbet.orbit.model.entity.to
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Dao
abstract class FeedDao(private val db: AppDatabase) {

    suspend fun getFeeds(): List<Feed> {
        return getFeedsImpl().map { it.to() }
    }

    suspend fun batchSave(feeds: List<Feed>) = withContext(Dispatchers.IO) {
        val sql = """
            INSERT INTO feeds (id, user_id, feed_url, site_url, title, 
            icon_url, error_count, error_msg, folder_id) VALUES 
            (?, ?, ?, ?, ?,     ?, ?, ?, ?)
        """
        feeds.forEach { feed ->
            val rowsUpdated = updateFeed(feed.feedURL, feed.siteURL, feed.title,
                feed.errorCount, feed.errorMsg, feed.iconURL,
                feed.folderId, feed.id
            )
            if (rowsUpdated == 0) {
                val args = arrayOf<Any>(
                    feed.id,
                    feed.userId,
                    feed.feedURL,
                    feed.siteURL,
                    feed.title,
                    feed.iconURL,
                    feed.errorCount,
                    feed.errorMsg,
                    feed.folderId,
                )
                db.openHelper.writableDatabase.execSQL(sql, args)
            }

        }
    }

    @Query("delete from feeds")
    abstract suspend fun clearAll()

    @Query("""
        update feeds set feed_url = :feedURL, site_url = :siteURL,
            title = :title, error_count = :errorCount, error_msg = :errorMsg,
            icon_url = :iconURL, folder_id = :folderId
        where id = :id
    """)
    abstract suspend fun updateFeed(feedURL: String, siteURL: String, title: String, errorCount: Int,
                                    errorMsg: String, iconURL: String, folderId: Long,
                                    id: Long
    ): Int


    @Query("select * from feeds")
    internal abstract suspend fun getFeedsImpl(): List<FeedEntity>

}