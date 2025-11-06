package cn.coolbet.orbit.common

import android.util.Base64
import cn.coolbet.orbit.remote.SessionAwareIconApi
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.key.Keyer
import coil3.map.Mapper
import coil3.request.Options
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.FileSystem

//@Composable
//fun rememberMinifluxIconImageLoader(): ImageLoader {
//    val context = LocalContext.current
//    val profileApi: ProfileApi = remember { MinifluxClient.provideProfileApi() }
//    return remember {
//        ImageLoader.Builder(context).components {
//            add(MinifluxIconURLMapper())
//            add(MinifluxIconFetcher.Factory(profileApi))
//            add(MinifluxIconKeyer())
//        }
//            .diskCachePolicy(CachePolicy.ENABLED)
//            .logger(DebugLogger(minLevel = Logger.Level.Debug))
//            .build()
//    }
//}

class MinifluxIconKeyer : Keyer<MinifluxIconURLModel> {
    override fun key(data: MinifluxIconURLModel, options: Options): String {
        return data.url
    }
}
data class MinifluxIconURLModel(val url: String)
class MinifluxIconFetcher @OptIn(ExperimentalCoilApi::class) constructor(
    private val iconURL: MinifluxIconURLModel,
    private val iconApi: SessionAwareIconApi,
    private val diskCache: Lazy<DiskCache?>,
    private val options: Options,
) : Fetcher {

    override suspend fun fetch(): FetchResult? = withContext(Dispatchers.IO) {
        var snapshot = readFromDiskCache()
        if (snapshot != null) {
            if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                return@withContext SourceFetchResult(
                    source = snapshot.toImageSource(),
                    mimeType = "image/*",
                    dataSource = DataSource.DISK,
                )
            }
            var mimeType = ""
            fileSystem.read(snapshot.metadata) {
                mimeType = this.readUtf8LineStrict()
            }
            return@withContext SourceFetchResult(
                source = snapshot.toImageSource(),
                mimeType = mimeType,
                dataSource = DataSource.DISK,
            )
        }

        if (!options.networkCachePolicy.readEnabled) {
            return@withContext null
        }

        val rep = iconApi.icon(iconURL.url)
        val byteArray = Base64.decode(rep.data.split("base64,")[1], Base64.NO_WRAP)
        val buffer: Buffer = Buffer().write(byteArray)

        snapshot = writeToDiskCache(snapshot, rep.mimeType, buffer)
        if (snapshot != null) {
            return@withContext SourceFetchResult(
                source = snapshot.toImageSource(),
                mimeType = rep.mimeType,
                dataSource = DataSource.DISK,
            )
        }
        SourceFetchResult(
            source = buffer.toImageSource(),
            mimeType = rep.mimeType,
            dataSource = DataSource.NETWORK
        )
    }

    private fun readFromDiskCache(): DiskCache.Snapshot? {
        if (options.diskCachePolicy.readEnabled) {
            return diskCache.value?.openSnapshot(diskCacheKey)
        } else {
            return null
        }
    }

    private fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        mimeType: String,
        data: Buffer,
    ): DiskCache.Snapshot? {
        if (!options.diskCachePolicy.writeEnabled) {
            return null
        }

        val editor = if (snapshot != null) {
            snapshot.closeAndOpenEditor()
        } else {
            diskCache.value?.openEditor(diskCacheKey)
        } ?: return null

        try {
            fileSystem.write(editor.metadata) {
                this.writeUtf8(mimeType)
            }
            fileSystem.write(editor.data) {
                data.readAll(this)
            }
            return editor.commitAndOpenSnapshot()
        } catch (e: Exception) {
            throw e
        }
    }

    private fun Buffer.toImageSource(): ImageSource {
        return ImageSource(
            source = this,
            fileSystem = fileSystem,
        )
    }

    private fun DiskCache.Snapshot.toImageSource(): ImageSource {
        return ImageSource(
            file = data,
            fileSystem = fileSystem,
            diskCacheKey = diskCacheKey,
            closeable = this,
        )
    }

    private val diskCacheKey: String
        get() = options.diskCacheKey ?: iconURL.url

    private val fileSystem: FileSystem
        get() = diskCache.value?.fileSystem ?: options.fileSystem

    class Factory(private val iconApi: SessionAwareIconApi) : Fetcher.Factory<MinifluxIconURLModel> {
        override fun create(data: MinifluxIconURLModel, options: Options, imageLoader: ImageLoader): Fetcher {
            return MinifluxIconFetcher(data, iconApi,
                diskCache = lazy { imageLoader.diskCache }, options = options,
            )
        }
    }

}
class MinifluxIconURLMapper : Mapper<String, MinifluxIconURLModel> {

    override fun map(data: String, options: Options): MinifluxIconURLModel? {
        if (!data.startsWith("v1/", ignoreCase = true)) {
            return null // 如果不是，返回 null，让 Coil 尝试其他 Mapper/Fetcher (例如默认的网络 Fetcher)
        }
        return MinifluxIconURLModel(url = data)
    }
}
class SimpleMimeTypeMetadata(val mimeType: String) : ImageSource.Metadata() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleMimeTypeMetadata) return false
        return mimeType == other.mimeType
    }

    override fun hashCode(): Int {
        return mimeType.hashCode()
    }
}
