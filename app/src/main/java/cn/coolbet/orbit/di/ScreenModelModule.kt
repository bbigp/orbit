package cn.coolbet.orbit.di

import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.ui.view.listdetail.ListDetailScreenModel
import cn.coolbet.orbit.ui.view.content.ContentScreenModel
import cn.coolbet.orbit.ui.view.feed.EditFeedContent
import cn.coolbet.orbit.ui.view.feed.EditFeedScreenModel
import cn.coolbet.orbit.ui.view.feed.EditFeedState
import cn.coolbet.orbit.ui.view.home.HomeScreenModel
import cn.coolbet.orbit.ui.view.listdetail.setting.ListDetailSettingScreenModel
import cn.coolbet.orbit.ui.view.login.LoginScreenModel
import cn.coolbet.orbit.ui.view.profile.ProfileScreenModel
import cn.coolbet.orbit.ui.view.search_entries.SearchEntriesScreenModel
import cn.coolbet.orbit.ui.view.sync.SyncScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val screenModelModule = module {
    factoryOf(::HomeScreenModel)
    factoryOf(::ProfileScreenModel)
    factory { LoginScreenModel(get(Qualifiers.NetworkApp), get()) }
    factoryOf(::SyncScreenModel)
    factoryOf(::ContentScreenModel)
    factory { (metaId: MetaId) ->
        ListDetailScreenModel(
            metaId,
            entryManager = get(),
            cacheStore = get(),
            eventBus = get(),
            ldSettingsDao = get(),
            coordinator = get()
        )
    }
    factory { (meta: Meta) ->
        SearchEntriesScreenModel(
            meta,
            searchDao = get(),
            entryManager = get(),
            session = get(),
            coordinator = get(),
            eventBus = get()
        )
    }
    factoryOf(::ListDetailSettingScreenModel)
    factory { (state: EditFeedState, content: EditFeedContent) ->
        EditFeedScreenModel(
            state, content, get()
        )
    }
    factory { (feed: Feed) ->

    }
}