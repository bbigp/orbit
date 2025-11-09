package cn.coolbet.orbit.remote

import cn.coolbet.orbit.remote.miniflux.MiniEntryApi
import cn.coolbet.orbit.remote.miniflux.to
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class EntryApi @Inject constructor(
    private val apiProvider: Provider<MiniEntryApi>
){
    suspend fun getEntries(page: Int = 1, size: Int = 10, statuses: List<String> = listOf("unread", "read", "removed"),
        order: String = "changed_at", direction: String = "desc", globallyVisible: Boolean = false,
    ) = withContext(Dispatchers.IO) {
        val page = apiProvider.get().getEntries(size, (page - 1) * size, statuses, order, direction, globallyVisible)
        if (page.total <= 0) return@withContext emptyList()
        page.entries.map { it.to() }
    }
}