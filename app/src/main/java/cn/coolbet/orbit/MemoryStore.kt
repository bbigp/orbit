package cn.coolbet.orbit

import android.util.Log
import cn.coolbet.orbit.dao.FeedMapper
import cn.coolbet.orbit.dao.FolderMapper
import cn.coolbet.orbit.manager.PreferenceManager
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Folder
import cn.coolbet.orbit.model.domain.User
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
class MemoryStore @Inject constructor(
    private val feedMapper: FeedMapper,
    private val folderMapper: FolderMapper,
    private val preferenceManager: PreferenceManager,
) {
    private val _feeds = MutableStateFlow<List<Feed>>(emptyList())
    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    private val _user = MutableStateFlow(User.EMPTY)
    private val mutex = Mutex()

    private val storeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    @Volatile private var currentLoadJob: Job? = null

    fun allFeeds(): Flow<List<Feed>> = _feeds.asStateFlow()
    fun allFolders(): Flow<List<Folder>> = _folders.asStateFlow()
    fun currentUser(): Flow<User> = _user.asStateFlow()

    fun feed(id: Long): Flow<Feed> {
        return _feeds.map { feeds ->
            feeds.find { it.id == id } ?: Feed.EMPTY
        }
    }

    fun folder(id: Long): Flow<Folder> {
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

    fun loadInitialData() {
        currentLoadJob?.cancel()
        currentLoadJob = storeScope.launch {
            val user = preferenceManager.userProfile()
            if (user.isEmpty) {
                Log.i("store", "未登录，无法预加载")
                return@launch
            }
            _user.value = user
            _feeds.value = feedMapper.getFeeds()
            val folders = folderMapper.getFolders()
            _folders.value = associateFeedsWithFolders(_feeds.value, folders)
            Log.i("store", "预加载完成 feed: ${_feeds.value.size} folder: ${_folders.value.size}")
        }
    }

    fun clear() {
        _feeds.value = emptyList()
        _folders.value = emptyList()
        _user.value = User.EMPTY
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