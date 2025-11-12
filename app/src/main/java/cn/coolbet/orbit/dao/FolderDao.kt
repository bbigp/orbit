package cn.coolbet.orbit.dao

import androidx.room.Dao
import androidx.room.Query
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.entity.FolderEntity
import cn.coolbet.orbit.model.entity.to
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Dao
abstract class FolderDao(private val db: AppDatabase) {

    suspend fun getFolders(): List<Folder> {
        return getFoldersImpl().map { it.to() }
    }

    suspend fun batchSave(items: List<Folder>) = withContext(Dispatchers.IO) {
        val sql = """
            INSERT INTO folders (id, user_id, title, hide_globally) VALUES 
            (?, ?, ?, ?) ON CONFLICT(id) DO UPDATE SET 
            title = excluded.title, hide_globally = excluded.hide_globally
        """
        for (item in items) {
            val args = arrayOf<Any>(item.id, item.userId, item.title, item.hideGlobally)
            db.openHelper.writableDatabase.execSQL(sql, args)
        }
    }

    @Query("delete from folders")
    abstract suspend fun clearAll()



    @Query("select * from folders order by id desc")
    internal abstract suspend fun getFoldersImpl(): List<FolderEntity>
}