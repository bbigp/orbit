package cn.coolbet.orbit.ui.view.content.extractor

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

enum class Extractor { Mercury, Readability }

@Singleton
class Oeeeed @Inject constructor(@ApplicationContext private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * 预热提取器
     */
    fun warmup(extractor: Extractor = Extractor.Mercury) {
        when (extractor) {
            Extractor.Mercury -> MercuryExtractor.shared(context = context).warmUp()
            else -> ""
        }
        Log.i("oeeeed", "---")
    }

    /**
     * 核心提取方法：使用挂起函数 (suspend)
     */
    private suspend fun extractArticleContent(
        url: String,
        html: String,
        extractor: Extractor = Extractor.Mercury
    ): ExtractedContent = withContext(Dispatchers.Main) {
        val result = when (extractor) {
            Extractor.Mercury -> MercuryExtractor.shared(context = context).extract(url = url, html = html)
            else -> null
        }
        return@withContext result ?: throw ExtractionError.FailedToExtract
    }

    /**
     * 获取并提取内容：组合异步操作
     */
    suspend fun fetchAndExtractContent(
        requestId: Long,
        url: String,
        extractor: Extractor = Extractor.Mercury
    ): ReadableDoc = withContext(Dispatchers.IO) {
        // 1. 预热（切换回主线程）
        withContext(Dispatchers.Main) { warmup(extractor) }

        // 2. HTTP 请求 (类似于 URLSession.data)
        var baseURL: String = url
        val html = runCatching {
            val request = Request.Builder().url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) ...") // 建议带上 UA 减少被封几率
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw ExtractionError.NetworkError(Exception("HTTP ${response.code}"))
                baseURL = response.request.url.toString()
                response.body?.string() ?: throw ExtractionError.DataIsNotString
            }
        }.getOrElse { e ->
            when(e) {
                is CancellationException -> throw e
                is ExtractionError -> throw e
                else -> throw ExtractionError.NetworkError(e)
            }
        }

        // 3. 异步提取内容
        val extracted = extractArticleContent(baseURL, html, extractor)

        // 4. 并行或顺序提取元数据 (async 可以并发执行)
//        val extractedMetadata = try {
//            SiteMetadata.extractMetadata(html, baseURL)
//        } catch (e: Exception) {
//            SiteMetadata(url)
//        }
        val metadata = SiteMetadata()

        // 5. 构造结果对象
        return@withContext ReadableDoc(
            metadata = metadata,
            extracted = extracted,
            url = url,
            requestId = requestId
        )
    }
}

sealed class ExtractionError : Exception() {
    object FailedToExtract : ExtractionError() {
        private fun readResolve(): Any = FailedToExtract
    }

    object DataIsNotString : ExtractionError() {
        private fun readResolve(): Any = DataIsNotString
    }

    object MissingExtractionData : ExtractionError() {
        private fun readResolve(): Any = MissingExtractionData
    }

    data class NetworkError(override val cause: Throwable) : ExtractionError()
}