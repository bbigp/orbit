package cn.coolbet.orbit.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.coolbet.orbit.model.domain.EntryStatusConverter
import cn.coolbet.orbit.model.domain.ReaderPageStateConverters
import cn.coolbet.orbit.model.entity.EntryEntity
import cn.coolbet.orbit.model.entity.FeedEntity
import cn.coolbet.orbit.model.entity.FolderEntity
import cn.coolbet.orbit.model.entity.LDSettings
import cn.coolbet.orbit.model.entity.LDSettingsConverters
import cn.coolbet.orbit.model.entity.MediaEntity
import cn.coolbet.orbit.model.entity.SearchRecord
import cn.coolbet.orbit.model.entity.SyncTaskRecord

const val DATABASE_NAME = "orbit_db"

@Database(
    entities = [
        FeedEntity::class, FolderEntity::class, SyncTaskRecord::class, EntryEntity::class,
        MediaEntity::class, SearchRecord::class,
        LDSettings::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(value = [
    EntryStatusConverter::class,
    LDSettingsConverters::class,
    ReaderPageStateConverters::class,
])
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
    abstract fun folderDao(): FolderDao
    abstract fun syncTaskRecordDao(): SyncTaskRecordDao
    abstract fun entryDao(): EntryDao
    abstract fun mediaDao(): MediaDao
    abstract fun searchDao(): SearchDao
    abstract fun lDSettingsDao(): LDSettingsDao
}


val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `entries_new` (
                `id` INTEGER NOT NULL,
                `user_id` INTEGER NOT NULL,
                `hash` TEXT NOT NULL,
                `feed_id` INTEGER NOT NULL DEFAULT 0,
                `status` TEXT NOT NULL DEFAULT 'unread',
                `title` TEXT NOT NULL DEFAULT '',
                `url` TEXT NOT NULL DEFAULT '',
                `published_at` INTEGER NOT NULL DEFAULT 0,
                `content` TEXT NOT NULL DEFAULT '',
                `author` TEXT NOT NULL DEFAULT '',
                `starred` INTEGER NOT NULL DEFAULT 0,
                `reading_time` INTEGER NOT NULL DEFAULT 0,
                `tags` TEXT NOT NULL DEFAULT '',
                `created_at` INTEGER NOT NULL DEFAULT 0,
                `changed_at` INTEGER NOT NULL DEFAULT 0,
                `summary` TEXT NOT NULL DEFAULT '',
                `readable_content` TEXT NOT NULL DEFAULT '',
                `lead_image_url` TEXT NOT NULL DEFAULT '',
                `readable_content_state` TEXT NOT NULL DEFAULT 'Idle',
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO `entries_new` (
                `id`, `user_id`, `hash`, `feed_id`, `status`,
                `title`, `url`, `published_at`, `content`, `author`,
                `starred`, `reading_time`, `tags`, `created_at`, `changed_at`,
                `summary`, `readable_content`, `lead_image_url`, `readable_content_state`
            )
            SELECT
                `id`, `user_id`, `hash`, `feed_id`, `status`,
                `title`, `url`, `published_at`, `content`, `author`,
                `starred`, `reading_time`, `tags`, `created_at`, `changed_at`,
                `summary`, `readable_content`, `lead_image_url`, `reader_page_state`
            FROM `entries`
            """.trimIndent()
        )
        db.execSQL("DROP TABLE `entries`")
        db.execSQL("ALTER TABLE `entries_new` RENAME TO `entries`")
    }
}

//@Module
//@InstallIn(SingletonComponent::class)
//object RoomModule {
//
//    @Provides
//    @Singleton
//    fun provideRFeedDao(database: AppDatabase): FeedDao {
//        return database.feedDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideRFolderDao(database: AppDatabase): FolderDao {
//        return database.folderDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideSyncTaskRecordDao(database: AppDatabase): SyncTaskRecordDao {
//        return database.syncTaskRecordDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideEntryDao(database: AppDatabase): EntryDao {
//        return database.entryDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideMediaDao(database: AppDatabase): MediaDao {
//        return database.mediaDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideSearchDao(database: AppDatabase): SearchDao {
//        return database.searchDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideLDSettingsDao(database: AppDatabase): LDSettingsDao {
//        return database.lDSettingsDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
//        return Room.databaseBuilder(
//            context,
//            AppDatabase::class.java,
//            DATABASE_NAME
//        )
////            .addMigrations(MIGRATION_1_2)
//            .build()
//    }
//}
