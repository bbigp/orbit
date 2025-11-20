package cn.coolbet.orbit.ui.view.entries

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cn.coolbet.orbit.common.BasePagingScreenModel
import cn.coolbet.orbit.common.PageState
import cn.coolbet.orbit.dao.EntryDao
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.model.domain.Entry
import javax.inject.Inject

class EntriesScreenModel @Inject constructor(
    private val entryManager: EntryManager,
): BasePagingScreenModel<Entry, Unit>(initialState = PageState(extra = Unit)) {

    init {
        loadInitialData()
    }

    override suspend fun fetchData(page: Int, size: Int): List<Entry> {
        return entryManager.getPage(page = page, size = size)
    }

}