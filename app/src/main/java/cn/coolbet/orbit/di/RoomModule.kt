package cn.coolbet.orbit.di

import androidx.room.Room
import cn.coolbet.orbit.dao.AppDatabase
import cn.coolbet.orbit.dao.DATABASE_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val roomModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            DATABASE_NAME
        )
//            .addMigrations(MIGRATION_1_2)
            .build()
    }
    single { get<AppDatabase>().feedDao() }
    single { get<AppDatabase>().folderDao() }
    single { get<AppDatabase>().syncTaskRecordDao() }
    single { get<AppDatabase>().entryDao() }
    single { get<AppDatabase>().mediaDao() }
    single { get<AppDatabase>().searchDao() }
    single { get<AppDatabase>().lDSettingsDao() }
}