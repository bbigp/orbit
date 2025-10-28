package cn.coolbet.orbit

import cn.coolbet.orbit.dao.FeedMapper
import cn.coolbet.orbit.dao.FolderMapper
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryStore @Inject constructor(
    private val feedMapper: FeedMapper,
    private val folderMapper: FolderMapper
) {
    private val _feeds = MutableStateFlow<List<Feed>>(emptyList())
    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    private val mutex = Mutex()

    private val storeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        storeScope.launch { loadInitialData() }
    }

    fun allFeeds(): Flow<List<Feed>> = _feeds.asStateFlow()
    fun allFolders(): Flow<List<Folder>> = _folders.asStateFlow()

    fun feed(id: Long): Flow<Feed> {
        return _feeds.map { feeds ->
            feeds.find { it.id == id } ?: Feed.EMPTY
        }
    }

    suspend fun updateFeed(id: Long, title: String) {
        mutex.withLock {
            _feeds.update { currentFeeds ->
                currentFeeds.map { feed ->
                    if (feed.id == id) feed.copy(title = title) else feed
                }
            }
        }
    }

    suspend fun loadInitialData() {
        _feeds.value = feedMapper.getFeeds()
        val folders = folderMapper.getFolders()
        val folder = Folder(id = 1, title = "土豆")
        val folder1 = Folder(id = 2, title = "All")
        _folders.value = listOf(folder, folder1.copy(feeds = _feeds.value))
    }
}