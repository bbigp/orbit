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
import cn.coolbet.orbit.ui.view.syncer.SyncViewModel
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

//class MainActivity : ComponentActivity() {
//    private val repository: FeedRepository by inject()
//}
//val screenModel = koinScreenModel<EditFeedScreenModel> { parametersOf(feedId) }

val appModule = module {
    single { GsonBuilder().create() }
    single { WorkManager.getInstance(androidContext()) }
    single { CoroutineScope(SupervisorJob() + Dispatchers.IO) }


    singleOf(::CacheStore)
    singleOf(::EntryManager)
    single { EventBus() }
    singleOf(::ListDetailCoordinator)
    singleOf(::LocalDataManager)
    single { Preference(androidContext(), get()) }
    singleOf(::Session)
    single { Oeeeed(androidContext()) }

    singleOf(::EntryApi)
    singleOf(::FeedApi)
    singleOf(::FolderApi)

}

val workerModule = module {
    workerOf(::SyncWorker)
}

val viewModelModule = module {
    // 方案 A：极致简洁（推荐）
    viewModelOf(::SyncViewModel)

    // 方案 B：显式声明（如果你需要对参数做特殊处理）
    // viewModel { SyncViewModel(get(), get(), get()) }
}