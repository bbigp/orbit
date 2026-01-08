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
            INSERT INTO folders (id, user_id, title) VALUES 
            (?, ?, ?)
        """
        items.forEach { folder ->
            val rowsUpdated = updateFolder(folder.title, folder.id)
            if (rowsUpdated == 0) {
                val args = arrayOf<Any>(folder.id, folder.userId, folder.title)
                db.openHelper.writableDatabase.execSQL(sql, args)
            }

        }
    }

    @Query("delete from folders")
    abstract suspend fun clearAll()

    @Query("""
        update folders set title = :title where id = :id
    """)
    abstract suspend fun updateFolder(title: String, id: Long): Int



    @Query("select * from folders order by id desc")
    internal abstract suspend fun getFoldersImpl(): List<FolderEntity>
}