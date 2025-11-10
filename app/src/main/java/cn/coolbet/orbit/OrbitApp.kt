package cn.coolbet.orbit

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import cn.coolbet.orbit.common.MinifluxIconFetcher
import cn.coolbet.orbit.common.MinifluxIconKeyer
import cn.coolbet.orbit.common.MinifluxIconURLMapper
import cn.coolbet.orbit.manager.Session
import cn.coolbet.orbit.remote.miniflux.MinIconFileApi
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.CachePolicy
import coil3.util.DebugLogger
import coil3.util.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class OrbitApp : Application(), SingletonImageLoader.Factory, Configuration.Provider {
    @Inject lateinit var session: Session
    @Inject lateinit var minIconFileApi: MinIconFileApi
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        session.startSession()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
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
            Log.d("WorkConfig", "Using HiltWorkerFactory: $workerFactory")
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        }
}