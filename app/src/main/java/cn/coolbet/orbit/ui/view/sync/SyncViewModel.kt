package cn.coolbet.orbit.ui.view.sync

import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import cn.coolbet.orbit.dao.SyncTaskRecordDao
import cn.coolbet.orbit.manager.IGNORE_TIME_KEY
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.manager.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SYNC_WORK_TAG = "data_sync_on_home_entry_tag"

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val session: Session,
    private val workManager: WorkManager,
    private val syncTaskRecordDao: SyncTaskRecordDao,
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

    fun syncData(checkLastExecuteTime: Boolean = true) {
        val user = session.user
        if (user.isEmpty) {
            Log.i("sync", "未登录，无法同步")
            return
        }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            if (checkLastExecuteTime) {
                val record = syncTaskRecordDao.getLastRecord(user.id)
                Log.i("sync", "检查上次同步")
                if (record?.executeTime != null) {
                    val differenceMillis = now - record.executeTime
                    if (differenceMillis < DateUtils.HOUR_IN_MILLIS * 2) {
                        Log.i("sync", "同步跳过，last execute time: ${record.executeTime}, " +
                                "now: $now 时间间隔: ${(now - record.executeTime)/1000/60}分钟")
                        return@launch
                    }
                }
            }
//            val inputData = workDataOf(
//                IGNORE_TIME_KEY to ignoreLastSyncTime,
//            )
            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                // 您可以添加约束条件，例如只在网络连接时运行
                // .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag(SYNC_WORK_TAG)
//                .setInputData(inputData)
                .build()

            // 启动 WorkManager 任务
            workManager.beginUniqueWork(
                SYNC_WORK_TAG,
                ExistingWorkPolicy.KEEP,
                syncRequest
            ).enqueue()
            Log.i("sync", "同步结束")
        }
    }
}