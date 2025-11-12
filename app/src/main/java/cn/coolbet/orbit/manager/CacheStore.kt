package cn.coolbet.orbit.manager

import android.util.Log
import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.dao.FeedDao
import cn.coolbet.orbit.dao.FolderDao
import cn.coolbet.orbit.dao.MediaDao
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
    private val feedDao: FeedDao,
    private val folderDao: FolderDao,
    private val entryDao: EntryDao,
    private val mediaDao: MediaDao,
    eventBus: EventBus,
    appScope: CoroutineScope,
) {
    private val _feeds = MutableStateFlow<List<Feed>>(emptyList())
    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    private val _unreadCountMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val mutex = Mutex()

    private val storeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    @Volatile private var currentLoadJob: Job? = null

    init {
        eventBus.subscribe<Evt.CacheInvalidated>(appScope) { event ->
            this.loadInitialData(event.userId)
        }
    }

    fun flowAllFeeds(): Flow<List<Feed>> = _feeds.asStateFlow()
    fun flowAllFolders(): Flow<List<Folder>> = _folders.asStateFlow()

    fun flowFeed(id: Long): Flow<Feed> {
        return _feeds.map { feeds ->
            feeds.find { it.id == id } ?: Feed.EMPTY
        }
    }

    fun feed(id: Long): Feed {
        return _feeds.value.find { it.id == id } ?: Feed.EMPTY
    }

    fun flowFolder(id: Long): Flow<Folder> {
        return _folders.map { folders ->
            folders.find { it.id == id } ?: Folder.EMPTY
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
            val feeds = feedDao.getFeeds()
            val folders = folderDao.getFolders()
            _feeds.value = feeds
            _folders.value = associateFeedsWithFolders(feeds, folders)


            val feedUnreadCount = entryDao.countFeedUnread()
            val unreadMap = feedUnreadCount.associate { unreadCount -> "e${unreadCount.feedId}" to unreadCount.count }.toMutableMap()
            for (feed in feeds) {
                val key = "o${feed.folderId}"
                val oUnread = unreadMap.getOrElse(key, { 0 })
                val eUnread = unreadMap.getOrElse("e${feed.id}", { 0 })
                unreadMap[key] = oUnread + eUnread
            }
            _unreadCountMap.value = unreadMap
            Log.i("store", "预加载完成 feed: ${_feeds.value.size} folder: ${_folders.value.size}")
        }
    }

    fun clearCache() {
        _feeds.value = emptyList()
        _folders.value = emptyList()
    }

    suspend fun deleteLocalData() {
        feedDao.clearAll()
        folderDao.clearAll()
        entryDao.clearAll()
        mediaDao.clearAll()
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