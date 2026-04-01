package cn.coolbet.orbit.manager

import android.util.Log
import cn.coolbet.orbit.common.increment
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CacheStore(
    private val feedDao: FeedDao,
    private val folderDao: FolderDao,
    private val entryDao: EntryDao,
    private val mediaDao: MediaDao,
    private val localDataManager: LocalDataManager,
    eventBus: EventBus,
    appScope: CoroutineScope,
) {
    private val _feeds = MutableStateFlow<List<Feed>>(emptyList())
    val feedsState: StateFlow<List<Feed>> = _feeds.asStateFlow()

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val foldersState: StateFlow<List<Folder>> = _folders.asStateFlow()

    private val _unreadCountMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val unreadMapState: StateFlow<Map<String, Int>> = _unreadCountMap.asStateFlow()

    private val mutex = Mutex()
    private val storeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    @Volatile
    private var currentLoadJob: Job? = null

    init {
        eventBus
            .subscribe<Evt.CacheInvalidated>(appScope) { event ->
                Log.i("eventbus", "refresh cache")
                loadInitialData(event.userId)
            }
            .subscribe<Evt.EntryStatusUpdated>(appScope) { event ->
                Log.i("eventbus", "EntryStatusUpdated newStatus: ${event.status} ${event.entryId}")
                localDataManager.updateFlags(event.entryId, status = event.status)
                val count = if (event.status.isUnread) 1 else -1
                _unreadCountMap.increment(
                    mapOf(
                        "o${event.folderId}" to count,
                        "e${event.feedId}" to count,
                    )
                )
            }
    }

    fun feed(id: Long): Feed {
        return _feeds.value.find { it.id == id } ?: Feed.EMPTY
    }

    fun flowFeed(id: Long): Flow<Feed> {
        return _feeds.map { feeds ->
            feeds.find { it.id == id } ?: Feed.EMPTY
        }
    }

    fun folder(id: Long): Folder {
        return _folders.value.find { it.id == id } ?: Folder.EMPTY
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
                Log.i("store", "not logged in, skip preload")
                clearCache()
                return@launch
            }

            val feeds = feedDao.getFeeds(userId)
            val folders = folderDao.getFolders(userId)
            _feeds.value = associateFolderWithFeed(feeds, folders)
            _folders.value = associateFeedsWithFolders(feeds, folders)

            val feedUnreadCount = entryDao.countFeedUnread(userId)
            val unreadMap = feedUnreadCount
                .associate { unreadCount -> "e${unreadCount.feedId}" to unreadCount.count }
                .toMutableMap()

            for (feed in feeds) {
                val folderKey = "o${feed.folderId}"
                val folderUnread = unreadMap[folderKey] ?: 0
                val feedUnread = unreadMap["e${feed.id}"] ?: 0
                unreadMap[folderKey] = folderUnread + feedUnread
            }

            _unreadCountMap.value = unreadMap
            Log.i("store", "preload complete feed=${_feeds.value.size} folder=${_folders.value.size}")
        }
    }

    fun clearCache() {
        _feeds.value = emptyList()
        _folders.value = emptyList()
        _unreadCountMap.value = emptyMap()
    }

    suspend fun deleteLocalData() {
        feedDao.clearAll()
        folderDao.clearAll()
        entryDao.clearAll()
        mediaDao.clearAll()
        clearCache()
    }

    fun associateFolderWithFeed(
        feeds: List<Feed>,
        folders: List<Folder>,
    ): List<Feed> {
        val folderMap: Map<Long, Folder> = folders.associateBy { it.id }
        return feeds
            .asSequence()
            .map { it.copy(folder = folderMap[it.folderId] ?: Folder.EMPTY) }
            .toList()
    }

    fun associateFeedsWithFolders(
        feeds: List<Feed>,
        folders: List<Folder>,
    ): List<Folder> {
        val folderFeedsMap = feeds.groupBy { it.folderId }
        return folders
            .asSequence()
            .map { folder ->
                folder.copy(feeds = folderFeedsMap[folder.id] ?: emptyList())
            }
            .toList()
    }
}
