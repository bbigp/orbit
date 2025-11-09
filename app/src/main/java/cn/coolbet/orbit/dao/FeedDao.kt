package cn.coolbet.orbit.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.entity.FeedEntity
import cn.coolbet.orbit.model.entity.to
import cn.coolbet.orbit.model.entity.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedMapper @Inject constructor(
    private val dao: RFeedDao,
    private val db: AppDatabase,
) {
    suspend fun getFeeds(): List<Feed> = withContext(Dispatchers.IO) {
        dao.getFeeds().map { it.to() }
    }

    suspend fun clearAll() {
        dao.clearAll()
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
}

@Dao
interface RFeedDao {

    @Query("select * from feeds")
    suspend fun getFeeds(): List<FeedEntity>

    @Query("delete from feeds")
    suspend fun clearAll()

}