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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "orbit_db"

@Database(
    autoMigrations = [AutoMigration(from = 1, to = 2)],
    entities = [FeedEntity::class, FolderEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rFeedDao(): RFeedDao
    abstract fun rFolderDao(): RFolderDao
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
    fun provideRFeedDao(database: AppDatabase): RFeedDao {
        return database.rFeedDao()
    }

    @Provides
    @Singleton
    fun provideRFolderDao(database: AppDatabase): RFolderDao {
        return database.rFolderDao()
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