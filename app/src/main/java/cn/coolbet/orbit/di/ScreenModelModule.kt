package cn.coolbet.orbit.di

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import cn.coolbet.orbit.ui.view.home.HomeScreenModel
import cn.coolbet.orbit.ui.view.login.LoginScreenModel
import cn.coolbet.orbit.ui.view.profile.ProfileScreenModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ScreenModelModule {

    @Binds
    @IntoMap
    @ScreenModelKey(HomeScreenModel::class)
    abstract fun bindHomeModel(homeScreenModel: HomeScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(ProfileScreenModel::class)
    abstract fun bindProfileModel(profileScreenModel: ProfileScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(LoginScreenModel::class)
    abstract fun bindLoginModel(loginScreenModel: LoginScreenModel): ScreenModel
}