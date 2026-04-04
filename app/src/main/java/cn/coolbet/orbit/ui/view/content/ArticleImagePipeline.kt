package cn.coolbet.orbit.ui.view.content

import android.content.Context
import android.net.Uri
import android.util.LruCache
import android.webkit.WebResourceResponse
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object ArticleImagePipeline {

    private const val MEMORY_CACHE_SIZE = 16 * 1024 * 1024
    private const val BREW_SCHEME = "brew"
    private const val BREW_IMAGE_HOST = "image"

    private var client: OkHttpClient? = null

    private val memoryCache = object : LruCache<String, CachedResource>(MEMORY_CACHE_SIZE) {
        override fun sizeOf(key: String, value: CachedResource): Int = value.bytes.size
    }

    private val ownerCalls = ConcurrentHashMap<String, MutableMap<String, Call>>()

    @Synchronized
    private fun getClient(): OkHttpClient {
        client?.let { return it }
        val built = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build()
        client = built
        return built
    }

    fun loadImageFromBrewUrl(
        context: Context,
        ownerToken: String,
        brewUrl: String,
        requestHeaders: Map<String, String>,
    ): WebResourceResponse? {
        val parsed = parseBrewImageRequest(brewUrl) ?: return null
        return loadImageInternal(
            context = context,
            ownerToken = ownerToken,
            imageUrl = parsed.imageUrl,
            referer = parsed.referer,
            requestHeaders = requestHeaders
        )
    }

    fun cancelAll(ownerToken: String?) {
        if (ownerToken.isNullOrBlank()) return
        val calls = ownerCalls.remove(ownerToken)?.values?.toList().orEmpty()
        calls.forEach { it.cancel() }
    }

    private fun loadImageInternal(
        context: Context,
        ownerToken: String,
        imageUrl: String,
        referer: String?,
        requestHeaders: Map<String, String>,
    ): WebResourceResponse? {
        val effectiveReferer = referer
            ?: requestHeaders["Referer"]
            ?: runCatching { "https://${java.net.URL(imageUrl).host}/" }.getOrNull()
        val cacheKey = buildString {
            append(imageUrl)
            append("::")
            append(effectiveReferer.orEmpty())
        }

        synchronized(memoryCache) {
            memoryCache.get(cacheKey)?.let { cached ->
                return WebResourceResponse(
                    cached.mimeType,
                    cached.encoding,
                    ByteArrayInputStream(cached.bytes)
                )
            }
        }

        readFromGlobalDiskCache(context, cacheKey)?.let { cached ->
            synchronized(memoryCache) {
                memoryCache.put(cacheKey, cached)
            }
            return WebResourceResponse(
                cached.mimeType,
                cached.encoding,
                ByteArrayInputStream(cached.bytes)
            )
        }

        return runCatching {
            val req = Request.Builder().url(imageUrl).apply {
                requestHeaders.forEach { (k, v) -> header(k, v) }
                if (!effectiveReferer.isNullOrBlank()) {
                    header("Referer", effectiveReferer)
                }
            }.build()

            val callKey = System.nanoTime().toString()
            val call = getClient().newCall(req)
            registerCall(ownerToken, callKey, call)
            try {
                call.execute().use { resp ->
                    if (!resp.isSuccessful) return null
                    val body = resp.body ?: return null
                    val bytes = body.bytes()
                    if (bytes.isEmpty()) return null

                    val mime = resp.header("Content-Type")
                        ?.substringBefore(";")
                        ?.takeIf { it.startsWith("image/") }
                        ?: guessMimeFromUrl(imageUrl)
                    val encoding = resp.header("Content-Encoding")

                    val cached = CachedResource(bytes, mime, encoding)
                    synchronized(memoryCache) {
                        memoryCache.put(cacheKey, cached)
                    }

                    WebResourceResponse(
                        mime,
                        encoding,
                        ByteArrayInputStream(bytes)
                    )
                }
            } finally {
                unregisterCall(ownerToken, callKey)
            }
        }.getOrNull()
    }

    private fun registerCall(ownerToken: String, callKey: String, call: Call) {
        val calls = ownerCalls.getOrPut(ownerToken) { ConcurrentHashMap() }
        calls[callKey] = call
    }

    private fun unregisterCall(ownerToken: String, callKey: String) {
        val calls = ownerCalls[ownerToken] ?: return
        calls.remove(callKey)
        if (calls.isEmpty()) {
            ownerCalls.remove(ownerToken)
        }
    }

    private fun parseBrewImageRequest(urlString: String): BrewImageRequest? {
        val uri = Uri.parse(urlString)
        if (!uri.scheme.equals(BREW_SCHEME, ignoreCase = true)) return null
        if (!uri.host.equals(BREW_IMAGE_HOST, ignoreCase = true)) return null
        val queryMap = parseQuery(uri.query)
        val imageUrl = queryMap["url"]?.takeIf { it.isNotBlank() } ?: return null
        val referer = queryMap["referer"]?.takeIf { it.isNotBlank() }
        return BrewImageRequest(imageUrl = imageUrl, referer = referer)
    }

    private fun parseQuery(query: String?): Map<String, String> {
        if (query.isNullOrBlank()) return emptyMap()
        return query.split("&")
            .mapNotNull { part ->
                if (part.isBlank()) return@mapNotNull null
                val idx = part.indexOf('=')
                if (idx < 0) {
                    decodeUrlComponent(part) to ""
                } else {
                    decodeUrlComponent(part.substring(0, idx)) to decodeUrlComponent(part.substring(idx + 1))
                }
            }.toMap()
    }

    private fun decodeUrlComponent(value: String): String {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.name())
    }

    private fun guessMimeFromUrl(url: String): String {
        val ext = url.substringAfterLast('.', "").substringBefore('?').lowercase()
        return when (ext) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            "svg" -> "image/svg+xml"
            "avif" -> "image/avif"
            else -> "image/*"
        }
    }

    private fun getGlobalDiskCache(context: Context): DiskCache? {
        return runCatching {
            SingletonImageLoader.get(context).diskCache
        }.getOrNull()
    }

    private fun readFromGlobalDiskCache(context: Context, key: String): CachedResource? {
        val diskCache = getGlobalDiskCache(context) ?: return null
        val snapshot = diskCache.openSnapshot(key) ?: return null
        return runCatching {
            snapshot.use { snap ->
                val fs = diskCache.fileSystem
                var mimeType = "image/*"
                var encoding: String? = null

                fs.read(snap.metadata) {
                    val line1 = readUtf8Line()
                    val line2 = readUtf8Line()
                    if (!line1.isNullOrBlank()) mimeType = line1
                    if (!line2.isNullOrBlank()) encoding = line2
                }

                val bytes = fs.read(snap.data) {
                    readByteArray()
                }

                if (bytes.isEmpty()) return null
                CachedResource(bytes, mimeType, encoding)
            }
        }.getOrNull()
    }

}

private data class BrewImageRequest(
    val imageUrl: String,
    val referer: String?,
)

private data class CachedResource(
    val bytes: ByteArray,
    val mimeType: String,
    val encoding: String?
)
