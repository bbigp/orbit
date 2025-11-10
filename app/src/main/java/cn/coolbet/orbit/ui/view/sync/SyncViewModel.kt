package cn.coolbet.orbit.ui.view.sync

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import cn.coolbet.orbit.dao.SyncTaskRecordDao
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.manager.IGNORE_TIME_KEY
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.manager.SyncWorker
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import dagger.Module
import dagger.Provides
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

private const val SYNC_WORK_TAG = "data_sync_on_home_entry_tag"

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val session: Session,
    private val workManager: WorkManager,
): ViewModel() {

    val isSyncing: StateFlow<Boolean> = workManager
        .getWorkInfosByTagFlow(SYNC_WORK_TAG)
        .map { workInfos ->
            workInfos.any { it.state == WorkInfo.State.RUNNING }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun syncData(ignoreLastSyncTime: Boolean = false) {
        val user = session.user
        if (user.isEmpty) {
            Log.i("sync", "未登录，无法同步")
            return
        }
        val inputData = workDataOf(
            IGNORE_TIME_KEY to ignoreLastSyncTime,
        )
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            // 您可以添加约束条件，例如只在网络连接时运行
            // .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag(SYNC_WORK_TAG)
            .setInputData(inputData)
            .build()

        // 启动 WorkManager 任务
        workManager.beginUniqueWork(
            SYNC_WORK_TAG,
            ExistingWorkPolicy.KEEP,
            syncRequest
        ).enqueue()
    }
}