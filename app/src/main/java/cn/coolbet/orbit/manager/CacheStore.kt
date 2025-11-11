package cn.coolbet.orbit.manager

import android.util.Log
import cn.coolbet.orbit.dao.FeedMapper
import cn.coolbet.orbit.dao.FolderMapper
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
class CacheStore @Inject constructor(
    private val feedMapper: FeedMapper,
    private val folderMapper: FolderMapper,
    eventBus: EventBus,
    appScope: CoroutineScope,
) {
    private val _feeds = MutableStateFlow<List<Feed>>(emptyList())
    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    private val mutex = Mutex()

    private val storeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    @Volatile private var currentLoadJob: Job? = null

    init {
        eventBus.subscribe<Evt.CacheInvalidated>(appScope) { event ->
            this.loadInitialData(event.userId)
        }
    }

    fun allFeeds(): Flow<List<Feed>> = _feeds.asStateFlow()
    fun allFolders(): Flow<List<Folder>> = _folders.asStateFlow()

    fun feed(id: Long): Flow<Feed> {
        return _feeds.map { feeds ->
            feeds.find { it.id == id } ?: Feed.Companion.EMPTY
        }
    }

    fun folder(id: Long): Flow<Folder> {
        return _folders.map { folders ->
            folders.find { it.id == id } ?: Folder.Companion.EMPTY
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

    fun loadInitialData(userId: Long) {
        currentLoadJob?.cancel()
        currentLoadJob = storeScope.launch {
            if (userId == 0L) {
                Log.i("store", "未登录，无法预加载")
                return@launch
            }
            _feeds.value = feedMapper.getFeeds()
            val folders = folderMapper.getFolders()
            _folders.value = associateFeedsWithFolders(_feeds.value, folders)
            Log.i("store", "预加载完成 feed: ${_feeds.value.size} folder: ${_folders.value.size}")
        }
    }

    fun clearCache() {
        _feeds.value = emptyList()
        _folders.value = emptyList()
    }

    suspend fun deleteLocalData() {
        feedMapper.clearAll()
        folderMapper.clearAll()
        clearCache()
    }


    fun associateFeedsWithFolders(
        feeds: List<Feed>,
        folders: List<Folder>,
    ): List<Folder> {
        val folderFeedsMap = feeds.groupBy { it.folderId }
        return folders
            .asSequence()
            .map { item ->
                item.copy(
                    feeds = folderFeedsMap[item.id] ?: emptyList() // 建议设置为 emptyList() 而不是 null
                )
            }
            .toList()
    }
}