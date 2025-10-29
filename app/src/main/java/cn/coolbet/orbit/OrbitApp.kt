package cn.coolbet.orbit

import android.app.Application
import cn.coolbet.orbit.common.MinifluxIconFetcher
import cn.coolbet.orbit.common.MinifluxIconKeyer
import cn.coolbet.orbit.common.MinifluxIconURLMapper
import cn.coolbet.orbit.remote.miniflux.MinifluxClient
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.CachePolicy
import coil3.util.DebugLogger
import coil3.util.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OrbitApp : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
    }

    private val profileApi by lazy { MinifluxClient.provideProfileApi() }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context).components {
            add(MinifluxIconURLMapper())
            add(MinifluxIconFetcher.Factory(profileApi))
            add(MinifluxIconKeyer())
        }
            .diskCachePolicy(CachePolicy.ENABLED)
            .logger(DebugLogger(minLevel = Logger.Level.Debug))
            .build()
    }
}