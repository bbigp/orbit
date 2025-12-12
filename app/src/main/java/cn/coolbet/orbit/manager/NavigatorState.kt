package cn.coolbet.orbit.manager

import cafe.adriel.voyager.navigator.Navigator
import cn.coolbet.orbit.ui.view.entries.EntriesScreen
import cn.coolbet.orbit.ui.view.entries.EntriesState
import cn.coolbet.orbit.ui.view.search_entries.SearchEntriesScreen
import cn.coolbet.orbit.ui.view.search_entries.SearchEntriesState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigatorState @Inject constructor(
    eventBus: EventBus,
    appScope: CoroutineScope,
){
    val entriesUi: MutableStateFlow<EntriesState> = MutableStateFlow(EntriesState())
    val searchUi: MutableStateFlow<SearchEntriesState> = MutableStateFlow(SearchEntriesState())

    init {
        eventBus.subscribe<Evt.ScreenDisposeRequest>(appScope) { event ->
            delay(50)
            handleScreenDispose(event)
        }
    }

    private var currentNavigator: Navigator? = null

    fun attachNavigator(navigator: Navigator) {
        this.currentNavigator = navigator
    }

    fun handleScreenDispose(event: Evt.ScreenDisposeRequest) {
        val navigator = currentNavigator ?: return
        val isContained = navigator.items.any { screen ->
            screen::class.java.simpleName == event.screenName
        }
        if (isContained) return
        when(event.screenName) {
            EntriesScreen::class.java.simpleName -> {
                entriesUi.update { EntriesState() }
            }
            SearchEntriesScreen::class.java.simpleName -> {
                searchUi.update { SearchEntriesState() }
            }
            else -> {}
        }
    }
}