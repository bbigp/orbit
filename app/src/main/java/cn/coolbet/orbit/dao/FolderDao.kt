package cn.coolbet.orbit.dao

import androidx.room.Dao
import androidx.room.Query
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.entity.FolderEntity
import cn.coolbet.orbit.model.entity.to
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FolderMapper @Inject constructor(
    private val dao: RFolderDao,
    private val db: AppDatabase,
) {

    suspend fun getFolders(): List<Folder> = withContext(Dispatchers.IO) {
        dao.getFolders().map { it.to() }
    }
}

@Dao
interface RFolderDao {

    @Query("select * from folders order by id desc")
    suspend fun getFolders(): List<FolderEntity>;
}