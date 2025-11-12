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
            icon_url, error_count, error_msg, folder_id, hide_globally) VALUES 
            (?, ?, ?, ?, ?,     ?, ?, ?, ?, ?) ON CONFLICT(id) DO UPDATE SET 
            feed_url = excluded.feed_url, site_url = excluded.site_url,
            title = excluded.title, 
            error_count = excluded.error_count, error_msg = excluded.error_msg,
            icon_url = excluded.icon_url,
            folder_id = excluded.folder_id,
            hide_globally = excluded.hide_globally
        """
        for (feed in feeds) {
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
                feed.hideGlobally,
            )
            db.openHelper.writableDatabase.execSQL(sql, args)
        }
    }

    @Query("delete from feeds")
    abstract suspend fun clearAll()


    @Query("select * from feeds")
    internal abstract suspend fun getFeedsImpl(): List<FeedEntity>

}