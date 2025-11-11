package cn.coolbet.orbit.ui.view.sync

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

    override suspend fun fetchData(page: Int, size: Int): List<SyncTaskRecord> {
        return syncTaskRecordDao.getPage(page, size)
    }

}

