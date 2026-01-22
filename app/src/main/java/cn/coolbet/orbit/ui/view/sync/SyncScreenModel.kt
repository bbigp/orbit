package cn.coolbet.orbit.ui.view.sync

import android.util.Log
import cn.coolbet.orbit.common.IPagingScreenModel
import cn.coolbet.orbit.common.PageState
import cn.coolbet.orbit.dao.SyncTaskRecordDao
import cn.coolbet.orbit.model.entity.SyncTaskRecord

class SyncScreenModel(
    private val syncTaskRecordDao: SyncTaskRecordDao,
): IPagingScreenModel<SyncTaskRecord>(
    initialState = PageState()
) {
    init { loadInitialData() }

    override suspend fun fetchData(page: Int, size: Int): List<SyncTaskRecord> {
        Log.i("syncRecord", "$page")
        return syncTaskRecordDao.getPage(page, size)
    }

}

