package cn.coolbet.orbit.ui.view.sync

import android.util.Log
import cn.coolbet.orbit.common.BasePagingScreenModel
import cn.coolbet.orbit.common.PageState
import cn.coolbet.orbit.dao.SyncTaskRecordDao
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import javax.inject.Inject

class SyncScreenModel @Inject constructor(
    private val syncTaskRecordDao: SyncTaskRecordDao,
): BasePagingScreenModel<SyncTaskRecord, Unit>(
    initialState = PageState(extra = Unit)
) {
    init { loadInitialData() }

    override suspend fun fetchData(page: Int, size: Int): List<SyncTaskRecord> {
        Log.i("syncRecord", "$page")
        return syncTaskRecordDao.getPage(page, size)
    }

}

