package cn.coolbet.orbit.ui.view.addfeed

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.manager.CacheStore
import kotlinx.coroutines.launch

class AddFeedScreenModel(
    val state: AddFeedState,
    val content: AddFeedContent,
    val cacheStore: CacheStore,
) : ScreenModel {

    fun addFeed() {

    }
}

class AddFeedContent
