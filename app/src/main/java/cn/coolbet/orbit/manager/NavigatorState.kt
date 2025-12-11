package cn.coolbet.orbit.manager

import cn.coolbet.orbit.ui.view.entries.EntriesState
import cn.coolbet.orbit.ui.view.search_entries.SearchEntriesState
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigatorState @Inject constructor(){

    val entriesUi: MutableStateFlow<EntriesState> = MutableStateFlow(EntriesState())
    val searchUi: MutableStateFlow<SearchEntriesState> = MutableStateFlow(SearchEntriesState())
}