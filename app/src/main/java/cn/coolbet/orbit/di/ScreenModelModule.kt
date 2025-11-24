package cn.coolbet.orbit.di

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cn.coolbet.orbit.ui.view.entries.EntriesScreenModel
import cn.coolbet.orbit.ui.view.home.HomeScreenModel
import cn.coolbet.orbit.ui.view.login.LoginScreenModel
import cn.coolbet.orbit.ui.view.profile.ProfileScreenModel
import cn.coolbet.orbit.ui.view.search_entries.SearchEntriesScreenModel
import cn.coolbet.orbit.ui.view.sync.SyncScreenModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
interface ScreenModelModule {

    @Binds
    @IntoMap
    @ScreenModelKey(HomeScreenModel::class)
    fun bindHomeModel(homeScreenModel: HomeScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(ProfileScreenModel::class)
    fun bindProfileModel(profileScreenModel: ProfileScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(LoginScreenModel::class)
    fun bindLoginModel(loginScreenModel: LoginScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(SyncScreenModel::class)
    fun bindSyncModel(syncScreenModel: SyncScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(EntriesScreenModel::class)
    fun bindEntriesModel(entriesScreenModel: EntriesScreenModel): ScreenModel

    companion object {
        @Provides
        @IntoMap
        @ScreenModelFactoryKey(SearchEntriesScreenModel.Factory::class)
        fun provideSearchEntriesModelFactory(
            factory: SearchEntriesScreenModel.Factory
        ): ScreenModelFactory = factory
    }
}