package cn.coolbet.orbit.di

import androidx.work.WorkManager
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.manager.EventBus
import cn.coolbet.orbit.manager.ListDetailCoordinator
import cn.coolbet.orbit.manager.LocalDataManager
import cn.coolbet.orbit.manager.Preference
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.manager.SyncWorker
import cn.coolbet.orbit.remote.EntryApi
import cn.coolbet.orbit.remote.FolderApi
import cn.coolbet.orbit.remote.miniflux.FeedApi
import cn.coolbet.orbit.ui.view.content.extractor.Oeeeed
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

//class MainActivity : ComponentActivity() {
//    private val repository: FeedRepository by inject()
//}
//val screenModel = koinScreenModel<EditFeedScreenModel> { parametersOf(feedId) }

val appModule = module {
    single { GsonBuilder().create() }
    single { WorkManager.getInstance(androidContext()) }
    single { CoroutineScope(SupervisorJob() + Dispatchers.IO) }


    factoryOf(::CacheStore)
    factoryOf(::EntryManager)
    single { EventBus() }
    factoryOf(::ListDetailCoordinator)
    factoryOf(::LocalDataManager)
    single { Preference(androidContext(), get()) }
    factoryOf(::Session)
    single { Oeeeed(androidContext()) }

    factoryOf(::EntryApi)
    factoryOf(::FeedApi)
    factoryOf(::FolderApi)

}

val workerModule = module {
    workerOf(::SyncWorker)
}

//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides
//    @Singleton
//    fun provideGson(): Gson {
//        return GsonBuilder()
////            .registerTypeAdapter(
////                UnreadMark::class.java,
////                UnreadMarkAdapter(UnreadMark::class.java, UnreadMark.NUMBER)
////            )
//            .create()
//    }
//
//    @Provides
//    @Singleton
//    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
//        return WorkManager.getInstance(context)
//    }
//
//    @Provides
//    @Singleton
//    fun provideAppScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//
//}