package cn.coolbet.orbit.module

import coil3.ImageLoader
import coil3.fetch.FetchResult
import coil3.request.Options
import okhttp3.OkHttpClient
import coil3.fetch.Fetcher

import android.util.Base64 // Android SDK Base64
import androidx.compose.runtime.remember
import cn.coolbet.orbit.remote.miniflux.MinifluxClient
import cn.coolbet.orbit.remote.miniflux.ProfileApi
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import okio.Buffer
import okio.BufferedSource
import okio.FileSystem
import okio.Source
import org.json.JSONObject
import java.io.IOException

// 1. 自定义 Model：用于封装你的特殊 URL
data class Base64UrlModel(val url: String)

class Base64JsonFetcher(
    private val model: Base64UrlModel,
) : Fetcher {

    val profileApi: ProfileApi = MinifluxClient.provideProfileApi()

    // 核心方法，返回获取到的数据
    override suspend fun fetch(): FetchResult = withContext(Dispatchers.IO) {
        try {
            val byteArray = Base64.decode("", Base64.NO_WRAP)

            val bufferedSource: BufferedSource = Buffer()
                .write(byteArray)
                .buffer()
            val imageSource = ImageSource(
                source = bufferedSource,
                fileSystem = FileSystem.SYSTEM,
            )
            SourceFetchResult(
                source = imageSource,
                mimeType = "image/jpeg",
                dataSource = DataSource.NETWORK// 标记为网络源，实现磁盘缓存
            )
        } catch (e: Exception) {
            throw IOException("Failed to process Base64 data for ${model.url}", e)
        }
    }

    /**
     * ResourceFetcher.Factory：告诉 Coil 何时应该使用 Base64JsonFetcher。
     */
    class Factory() : Fetcher.Factory<Base64UrlModel> {
        override fun create(data: Base64UrlModel, options: Options, imageLoader: ImageLoader): Fetcher {
            return Base64JsonFetcher(data)
        }
    }

}