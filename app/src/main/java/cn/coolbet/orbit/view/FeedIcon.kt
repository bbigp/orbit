package cn.coolbet.orbit.view

import android.util.Base64
import android.util.Log
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.remote.miniflux.MinifluxClient
import cn.coolbet.orbit.remote.miniflux.ProfileApi
import cn.coolbet.orbit.ui.theme.ElementSize
import cn.coolbet.orbit.ui.theme.M11White00
import cn.coolbet.orbit.ui.theme.M15White00
import coil3.ColorImage
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.SubcomposeAsyncImage
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.key.Keyer
import coil3.map.Mapper
import coil3.request.CachePolicy
import coil3.request.Options
import coil3.util.DebugLogger
import coil3.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.FileSystem


enum class FeedIconSize (val size: Dp, val radius: Dp, val style: TextStyle) {
    SMALL( size = 18.dp, radius = 4.dp, style = M11White00),
    MEDIUM( size = 24.dp, radius = 6.dp, style = M15White00),
    LARGE( size = 36.dp, radius = 8.dp, style = M15White00);
    companion object {
        fun get(elementSize: ElementSize): FeedIconSize {
            return when (elementSize) {
                ElementSize.SMALL -> SMALL
                ElementSize.LARGE -> LARGE
                else -> MEDIUM
            }
        }
    }
}


@Composable
fun FeedIcon(url: String, alt: String, size: ElementSize = ElementSize.MEDIUM) {
    val miniIconImageLoader = rememberMinifluxIconImageLoader()
    val iconSize = FeedIconSize.get(size)
    Box(modifier = Modifier
        .size(iconSize.size)
        .clip(RoundedCornerShape(iconSize.radius)),
        contentAlignment = Alignment.Center) {
        SubcomposeAsyncImage(
            model = url,
            imageLoader = miniIconImageLoader,
            contentDescription = alt,
            modifier = Modifier.size(iconSize.size),
            contentScale = ContentScale.Crop,
            onError = {state ->
                Log.e("ImageLoadError", "Error: ${state.result.throwable.localizedMessage}")
            },
            loading = {
                Box(modifier = Modifier.size(iconSize.size).background(Color.LightGray))
                //列表滚动停止时，停止时：使用 Shimmer 动画
//                ShimmerContainer(size = iconSize.size)
            },
            error = {
                val initial = alt.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0x80555555), Color(0xBF555555))
                        )
                    ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = initial,
                        style = iconSize.style,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                    )
                }
            },
        )
    }
}


@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true)
@Composable
fun PreviewFeedIcon(){
    val previewHandler = AsyncImagePreviewHandler {
        ColorImage(Color.Red.toArgb())
    }
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        FeedIcon(url = "https://cdn-static.sspai.com/favicon/sspai.ico", alt = "少数派")
    }
}

@Composable
fun rememberMinifluxIconImageLoader(): ImageLoader {
    val context = LocalContext.current
    val profileApi: ProfileApi = remember { MinifluxClient.provideProfileApi() }
    return remember {
        ImageLoader.Builder(context).components {
            add(MinifluxIconURLMapper())
            add(MinifluxIconFetcher.Factory(profileApi))
            add(MinifluxIconKeyer())
        }
            .diskCachePolicy(CachePolicy.ENABLED)
            .logger(DebugLogger(minLevel = Logger.Level.Debug))
            .build()
    }
}

class MinifluxIconKeyer : Keyer<MinifluxIconURLModel> {
    override fun key(data: MinifluxIconURLModel, options: Options): String {
        return data.url
    }
}
data class MinifluxIconURLModel(val url: String)
class MinifluxIconFetcher @OptIn(ExperimentalCoilApi::class) constructor(
    private val iconURL: MinifluxIconURLModel,
    private val profileApi: ProfileApi,
    private val diskCache: Lazy<DiskCache?>,
    private val options: Options,
) : Fetcher {

    override suspend fun fetch(): FetchResult = withContext(Dispatchers.IO) {
        var snapshot = diskCache.value?.openSnapshot(diskCacheKey)
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

        val rep = profileApi.icon(iconURL.url)
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

    private fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        mimeType: String,
        data: Buffer,
    ): DiskCache.Snapshot? {

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

    class Factory(private val profileApi: ProfileApi) : Fetcher.Factory<MinifluxIconURLModel> {
        override fun create(data: MinifluxIconURLModel, options: Options, imageLoader: ImageLoader): Fetcher {
            return MinifluxIconFetcher(data, profileApi,
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


@Composable
fun ShimmerContainer(
    size: Dp,
    baseColor: Color = Color(0xFFE0E0E0),
    highlightColor: Color = Color(0xFFF5F5F5)
) {
    Box(
        modifier = Modifier
            .size(size) // 设置尺寸
            // 关键修正：在应用 shimmer 效果之前，确保内容被裁剪到边界内
            .clipToBounds()
            .shimmer(baseColor = baseColor, highlightColor = highlightColor)
    )
}



fun Modifier.shimmer(
    baseColor: Color,
    highlightColor: Color,
    animationDurationMillis: Int = 1200,
    shimmerWidthRatio: Float = 0.8f
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmerTransition")

    val xShimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -1.0f,
        targetValue = 1.0f + shimmerWidthRatio,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = animationDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "xShimmerTranslate"
    )

    // 修正：我们不在这里调用 clip()。
    // 我们将渐变作为背景应用，并让外部的 Modifier 负责裁剪。
    this
        .background(Color.White) // 首先给一个基础背景色 (可选)
        .drawWithCache {
            // 使用 drawWithCache 更精确地控制绘制，避免 graphicsLayer 导致的其他副作用
            val size = this.size
            onDrawBehind {
                val brush = Brush.linearGradient(
                    colors = listOf(baseColor, highlightColor, baseColor),
                    start = Offset(xShimmerTranslate * size.width, 0f),
                    end = Offset((xShimmerTranslate + shimmerWidthRatio) * size.width, size.height)
                )
                // 绘制渐变，它会被 Box 的 clip/clipToBounds 裁剪
                drawRect(brush = brush)
            }
        }
}