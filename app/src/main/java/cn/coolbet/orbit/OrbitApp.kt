package cn.coolbet.orbit

import android.app.Application
import cn.coolbet.orbit.common.MinifluxIconFetcher
import cn.coolbet.orbit.common.MinifluxIconKeyer
import cn.coolbet.orbit.common.MinifluxIconURLMapper
import cn.coolbet.orbit.manager.SessionManager
import cn.coolbet.orbit.remote.SessionAwareIconApi
import cn.coolbet.orbit.remote.miniflux.MinIconFileApi
import cn.coolbet.orbit.remote.miniflux.MinifluxClient
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.CachePolicy
import coil3.util.DebugLogger
import coil3.util.Logger
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class OrbitApp : Application(), SingletonImageLoader.Factory {
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var awareIconApi: SessionAwareIconApi

    override fun onCreate() {
        super.onCreate()
        sessionManager.startSession()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context).components {
            add(MinifluxIconURLMapper())
            add(MinifluxIconFetcher.Factory(awareIconApi))
            add(MinifluxIconKeyer())
        }
            .diskCachePolicy(CachePolicy.ENABLED)
            .logger(DebugLogger(minLevel = Logger.Level.Debug))
            .build()
    }
}