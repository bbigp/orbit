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
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Flow	Cold Stream（冷流）	Coroutines	异步数据流的基础。用于处理一次性数据序列（如 API 响应）或事件流（如点击事件）。
 * StateFlow	Hot Stream（热流）	Coroutines	只读的   可观察的状态持有者。始终包含一个最新值，并且会向新的收集者立即发送这个最新值。常用于 ViewModel。
 * MutableStateFlow	Hot Stream（热流）	Coroutines 刻修改的	StateFlow 的可变版本。用于在内部修改状态（通过设置其 .value 属性）。
 * State	Interface	Compose	Compose 运行时可追踪的状态接口。当它的 .value 属性被读取时，Compose 会记录下来，并在值改变时触发依赖它的 Composable 重组。
 * by	Kotlin Delegate	Kotlin	属性委托关键字。在 Compose 中，用于简化对 State<T> 值的访问。
 */
@Singleton
class CacheStore @Inject constructor(
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
    @Volatile private var currentLoadJob: Job? = null

    init {
        eventBus
            .subscribe<Evt.CacheInvalidated>(appScope) { event ->
                Log.i("eventbus", "刷新缓存")
                loadInitialData(event.userId)
            }
            .subscribe<Evt.EntryStatusUpdated>(appScope) { event ->
                Log.i("eventbus", "EntryStatusUpdated newStatus: ${event.status} ${event.entryId}")
                localDataManager.updateFlags(event.entryId, status = event.status)
                val count = if (event.status.isUnread) 1 else -1
                _unreadCountMap.increment(mapOf(
                    "o${event.folderId}" to count,
                    "e${event.feedId}" to count
                ))
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