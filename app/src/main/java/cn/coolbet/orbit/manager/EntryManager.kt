package cn.coolbet.orbit.manager

import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.dao.MediaDao
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.view.list_detail.item.LDMagazine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryManager @Inject constructor(
    private val entryDao: EntryDao,
    private val mediaDao: MediaDao,
    private val cacheStore: CacheStore,
) {

    suspend fun getPage(meta: Meta, page: Int = 1, size: Int = 20, search: String = ""): List<Entry> {
        val entries = entryDao.getEntries(page, size, ListDetailQuery(meta = meta, search = search))
        if (entries.isEmpty()) return emptyList()

        val mediaMap = mediaDao.getMap(entryIds = entries.map { it.id }.toList())
        return entries.map {
            it.copy(
                medias = mediaMap.getOrElse(it.id, { emptyList() }),
                feed = cacheStore.feed(it.feedId)
            )
        }
    }

    suspend fun getPage(query: ListDetailQuery, page: Int = 1, size: Int = 20): List<Entry> {
        val entries = entryDao.getEntries(page, size, query)
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

data class ListDetailQuery(
    val search: String = "",
    val meta: Meta,
    val settings: LDSettings = LDSettings.defaultSettings
)