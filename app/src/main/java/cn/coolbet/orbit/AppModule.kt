package cn.coolbet.orbit

import android.content.Context
import androidx.room.Room
import cn.coolbet.orbit.dao.AppDatabase
import cn.coolbet.orbit.dao.RFeedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


private const val DATABASE_NAME = "orbit_db"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


}