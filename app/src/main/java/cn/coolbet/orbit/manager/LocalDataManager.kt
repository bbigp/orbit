package cn.coolbet.orbit.manager

import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.model.domain.EntryStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataManager @Inject constructor(
    private val entryDao: EntryDao,
){

    suspend fun updateFlags(id: Long, status: EntryStatus? = null, starred: Boolean? = null) {
        entryDao.updateFlags(status?.value, starred, id)
        //todo  save record commit server
    }
}