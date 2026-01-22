package cn.coolbet.orbit

import android.app.Application
import android.util.Log
import android.webkit.WebView
import androidx.work.Configuration
import cn.coolbet.orbit.common.MinifluxIconFetcher
import cn.coolbet.orbit.common.MinifluxIconKeyer
import cn.coolbet.orbit.common.MinifluxIconURLMapper
import cn.coolbet.orbit.di.appModule
import cn.coolbet.orbit.di.networkModule
import cn.coolbet.orbit.di.roomModule
import cn.coolbet.orbit.di.screenModelModule
import cn.coolbet.orbit.di.viewModelModule
import cn.coolbet.orbit.di.workerModule
import cn.coolbet.orbit.manager.Env
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.remote.miniflux.MinIconFileApi
import cn.coolbet.orbit.remote.miniflux.minifluxModule
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.CachePolicy
import coil3.util.DebugLogger
import coil3.util.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class OrbitApp : Application(), SingletonImageLoader.Factory, Configuration.Provider,
    KoinComponent {

    override fun onCreate() {
        super.onCreate()
        Env.init(this)
        startKoin {
            androidContext(this@OrbitApp)
            androidLogger()
            workManagerFactory()
            modules(
                appModule, networkModule, screenModelModule, roomModule,
                workerModule, minifluxModule, viewModelModule
            )

            val session = getKoin().get<Session>()
            session.startSession()
        }
        WebView.setWebContentsDebuggingEnabled(true)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val minIconFileApi = getKoin().get<MinIconFileApi>()
        return ImageLoader.Builder(context).components {
            add(MinifluxIconURLMapper())
            add(MinifluxIconFetcher.Factory(minIconFileApi))
            add(MinifluxIconKeyer())
        }
            .diskCachePolicy(CachePolicy.ENABLED)
            .logger(DebugLogger(minLevel = Logger.Level.Debug))
            .build()
    }

    override val workManagerConfiguration: Configuration
        get() {
            val workerFactory = getKoin().get<KoinWorkerFactory>()
            Log.d("WorkConfig", "Using HiltWorkerFactory: $workerFactory")
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        }
}