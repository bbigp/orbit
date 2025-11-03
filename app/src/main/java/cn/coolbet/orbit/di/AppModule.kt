package cn.coolbet.orbit.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
//            .registerTypeAdapter(
//                UnreadMark::class.java,
//                UnreadMarkAdapter(UnreadMark::class.java, UnreadMark.NUMBER)
//            )
            .create()
    }

}