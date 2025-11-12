package cn.coolbet.orbit.dao

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.coolbet.orbit.model.entity.FeedEntity
import cn.coolbet.orbit.model.entity.FolderEntity
import cn.coolbet.orbit.model.entity.SyncTaskRecord
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "orbit_db"

@Database(
    entities = [FeedEntity::class, FolderEntity::class, SyncTaskRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
    abstract fun folderDao(): FolderDao
    abstract fun syncTaskRecordDao(): SyncTaskRecordDao
}


val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {

    }
}

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideRFeedDao(database: AppDatabase): FeedDao {
        return database.feedDao()
    }

    @Provides
    @Singleton
    fun provideRFolderDao(database: AppDatabase): FolderDao {
        return database.folderDao()
    }

    @Provides
    @Singleton
    fun provideSyncTaskRecordDao(database: AppDatabase): SyncTaskRecordDao {
        return database.syncTaskRecordDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
//            .addMigrations(MIGRATION_1_2)
            .build()
    }
}