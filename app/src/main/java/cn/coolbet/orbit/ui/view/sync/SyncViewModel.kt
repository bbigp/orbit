package cn.coolbet.orbit.ui.view.sync

import android.util.Log
import androidx.lifecycle.ViewModel
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.manager.SyncTaskRecordManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val syncTaskRecordManager: SyncTaskRecordManager,
    private val cacheStore: CacheStore,
    private val session: Session,
): ViewModel() {

    suspend fun syncData(force: Boolean = false) {
        syncTaskRecordManager.startTask(force)
    }
}