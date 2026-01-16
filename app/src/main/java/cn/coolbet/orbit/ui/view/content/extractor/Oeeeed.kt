package cn.coolbet.orbit.ui.view.content.extractor

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

enum class Extractor { Mercury, Readability }

class Oeeeed(val context: Context) {

    /**
     * 预热提取器
     */
    fun warmup(extractor: Extractor = Extractor.Mercury) {
        when (extractor) {
            Extractor.Mercury -> MercuryExtractor.shared(context = context).warmUp()
            else -> ""
        }
    }

    /**
     * 核心提取方法：使用挂起函数 (suspend)
     */
    suspend fun extractArticleContent(
        url: String,
        html: String,
        extractor: Extractor = Extractor.Mercury
    ): ExtractedContent = withContext(Dispatchers.Main) {
        // 使用 suspendCancellableCoroutine 替代 withCheckedThrowingContinuation
        suspendCancellableCoroutine { continuation ->
            val callback = { contentOpt: ExtractedContent? ->
                if (contentOpt != null) {
                    continuation.resume(contentOpt) { /* 取消时的清理逻辑 */ }
                } else {
                    continuation.resumeWithException(ExtractionError.FailedToExtract)
                }
            }

            when (extractor) {
                Extractor.Mercury -> MercuryExtractor.shared(context = context).extract(html, url, callback)
                else -> ""
            }
        }
    }

    /**
     * 获取并提取内容：组合异步操作
     */
    suspend fun fetchAndExtractContent(
        url: String,
        extractor: Extractor = Extractor.Mercury
    ): ReadableDoc = withContext(Dispatchers.IO) {
        // 1. 预热（切换回主线程）
        withContext(Dispatchers.Main) { warmup(extractor) }

        // 2. HTTP 请求 (类似于 URLSession.data)
        // 这里建议使用你的 OkHttpClient 或 Ktor
        val response = try {
            URL(url).readText() // 简单演示，生产环境建议用 OkHttp
        } catch (e: Exception) {
            throw ExtractionError.NetworkError(e)
        }

        val html = response
        val baseURL = url // 实际应从 response 获取

        // 3. 异步提取内容
        val content = extractArticleContent(baseURL, html, extractor)

        // 4. 并行或顺序提取元数据 (async 可以并发执行)
        val extractedMetadata = try {
            SiteMetadata.extractMetadata(html, baseURL)
        } catch (e: Exception) {
            SiteMetadata(url)
        }

        // 5. 构造结果对象
        return@withContext ReadableDoc(
            extracted = content,
            insertHeroImage = null,
            metadata = extractedMetadata,
            date = content.datePublished
        ) ?: throw ExtractionError.MissingExtractionData
    }

    // 定义相关的枚举和异常


    sealed class ExtractionError : Exception() {
        object FailedToExtract : ExtractionError()
        object DataIsNotString : ExtractionError()
        object MissingExtractionData : ExtractionError()
        data class NetworkError(val cause: Throwable) : ExtractionError()
    }
}

// 数据类定义
data class ReadableDoc(
    val extracted: ExtractedContent,
    val insertHeroImage: String?,
    val metadata: SiteMetadata,
    val date: String?
)