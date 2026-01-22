package cn.coolbet.orbit.remote

import cn.coolbet.orbit.remote.miniflux.MiniEntryApi
import cn.coolbet.orbit.remote.miniflux.to
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EntryApi(
    private val miniEntryApi: MiniEntryApi
){
    suspend fun getEntries(page: Int = 1, size: Int = 10, statuses: List<String> = listOf("unread", "read", "removed"),
        order: String = "changed_at", direction: String = "desc", globallyVisible: Boolean = false,
    ) = withContext(Dispatchers.IO) {
        val page = miniEntryApi.getEntries(size, (page - 1) * size, statuses, order, direction, globallyVisible)
        if (page.total <= 0) return@withContext emptyList()
        page.entries.map { it.to() }
    }
}