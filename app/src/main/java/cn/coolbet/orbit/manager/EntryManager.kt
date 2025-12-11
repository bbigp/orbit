package cn.coolbet.orbit.manager

import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.dao.MediaDao
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Meta
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryManager @Inject constructor(
    private val entryDao: EntryDao,
    private val mediaDao: MediaDao,
    private val cacheStore: CacheStore,
) {

    suspend fun getPage(mate: Meta, page: Int = 1, size: Int = 20, search: String = ""): List<Entry> {
        val entries = entryDao.getEntries(page, size, mate, search)
        if (entries.isEmpty()) return emptyList()

        val mediaMap = mediaDao.getMap(entryIds = entries.map { it.id }.toList())
        return entries.map {
            it.copy(
                medias = mediaMap.getOrElse(it.id, { emptyList() }),
                feed = cacheStore.feed(it.feedId)
            )
        }
    }

}