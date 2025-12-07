package cn.coolbet.orbit.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.domain.Media
import cn.coolbet.orbit.model.entity.FolderEntity
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import cn.coolbet.orbit.remote.EntryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Dao
abstract class SyncTaskRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(record: SyncTaskRecord): Long

    @Query("select * from sync_task_records order by id desc limit :limit offset :offset")
    abstract suspend fun getList(limit: Int, offset :Int): List<SyncTaskRecord>

    suspend fun getPage(page: Int, size: Int): List<SyncTaskRecord> {
        return getList(size, (page - 1) * size)
    }

    @Query("""
        update sync_task_records
        set entry = :entry, media = :media, feed = :feed, folder = :folder,
            error_msg = :errorMsg, from_time = :fromTime, to_time = :toTime,
            status = :status, changed_at = STRFTIME('%s', 'now') * 1000
        where id = :id
    """)
    abstract suspend fun updateFinish(entry: Int, media: Int, feed: Int, folder: Int, errorMsg: String = "",
                             fromTime: Long, toTime: Long, status: String, id: Long): Int


    @Query("SELECT COALESCE(MAX(execute_time), 0) FROM sync_task_records where user_id = :userId")
    abstract suspend fun getLastExecuteTime(userId: Long): Long

    @Query("select * from sync_task_records " +
            "where user_id = :userId and status in ('ok') " +
            "order by id desc limit 1")
    abstract suspend fun getLastRecord(userId: Long): SyncTaskRecord?

}