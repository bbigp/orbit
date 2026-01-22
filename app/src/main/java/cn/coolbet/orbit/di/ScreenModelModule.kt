package cn.coolbet.orbit.di

import cn.coolbet.orbit.model.domain.Meta
import cn.coolbet.orbit.model.domain.MetaId
import cn.coolbet.orbit.ui.view.list_detail.ListDetailScreenModel
import cn.coolbet.orbit.ui.view.content.ContentScreenModel
import cn.coolbet.orbit.ui.view.home.HomeScreenModel
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
}


//@Module
//@InstallIn(SingletonComponent::class)
//interface ScreenModelModule {
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(HomeScreenModel::class)
//    fun bindHomeModel(homeScreenModel: HomeScreenModel): ScreenModel
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(ProfileScreenModel::class)
//    fun bindProfileModel(profileScreenModel: ProfileScreenModel): ScreenModel
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(LoginScreenModel::class)
//    fun bindLoginModel(loginScreenModel: LoginScreenModel): ScreenModel
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(SyncScreenModel::class)
//    fun bindSyncModel(syncScreenModel: SyncScreenModel): ScreenModel
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(ContentScreenModel::class)
//    fun bindContentModel(contentScreenModel: ContentScreenModel): ScreenModel
//
//    companion object {
//
//        @Provides
//        @IntoMap
//        @ScreenModelFactoryKey(ListDetailScreenModel.Factory::class)
//        fun provideListDetailScreenModelFactory(
//            factory: ListDetailScreenModel.Factory
//        ): ScreenModelFactory = factory
//
//        @Provides
//        @IntoMap
//        @ScreenModelFactoryKey(SearchEntriesScreenModel.Factory::class)
//        fun provideSearchEntriesModelFactory(
//            factory: SearchEntriesScreenModel.Factory
//        ): ScreenModelFactory = factory
//    }
//}