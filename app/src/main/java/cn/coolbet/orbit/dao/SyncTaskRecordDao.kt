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
interface SyncTaskRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: SyncTaskRecord): Long

    @Query("select * from sync_task_records order by id desc limit :limit offset :offset")
    suspend fun getList(limit: Int, offset :Int): List<SyncTaskRecord>

    @Query("""
        update sync_task_records
        set entry = :entry, media = :media, feed = :feed, folder = :folder,
            error_msg = :errorMsg, from_time = :fromTime, to_time = :toTime,
            status = :status, changed_at = unixepoch('now') * 1000
        where id = :id
    """)
    suspend fun updateFinish(entry: Int, media: Int, feed: Int, folder: Int, errorMsg: String = "",
                             fromTime: Long, toTime: Long, status: String, id: Long): Int


    @Query("SELECT COALESCE(MAX(execute_time), 0) FROM sync_task_records where user_id = :userId")
    suspend fun getLastExecuteTime(userId: Long): Long

    @Query("select * from sync_task_records " +
            "where user_id = :userId and status in ('ok') " +
            "order by id desc limit 1")
    suspend fun getLastRecord(userId: Long): SyncTaskRecord?

}