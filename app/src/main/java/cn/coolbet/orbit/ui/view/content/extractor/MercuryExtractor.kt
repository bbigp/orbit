package cn.coolbet.orbit.ui.view.content.extractor

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.coolbet.orbit.common.readAssetText
import com.google.gson.Gson
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume


class MercuryExtractor private constructor(val context: Context){

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: MercuryExtractor? = null

        fun shared(context: Context): MercuryExtractor {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MercuryExtractor(context).also { INSTANCE = it }
            }
        }
    }

    private val webView = WebView(context.applicationContext)
    private val isReady = CompletableDeferred<Unit>()
    private val gson = Gson()

    private inner class MercuryBridge(private val onResult: (String, String) -> Unit) {
        @JavascriptInterface
        fun postMessage(name: String, payload: String) {
            onResult(name, payload)
        }
    }

    fun warmUp() {
        initializeJS()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeJS() {
        val mercuryJs = context.readAssetText("js/mercury.web.js")
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!isReady.isCompleted) {
                    isReady.complete(Unit)
                }
            }
        }

        val html = """
            <!DOCTYPE html>
            <html lang="en">
            <head></head>
            <body>
                <script>$mercuryJs</script>
                <script>alert('ok')</script>
            </body>
            </html>
        """.trimIndent()
        webView.loadData(html, "text/html", "UTF-8")
    }


    suspend fun extract(url: String, html: String): ExtractedContent? = withContext(Dispatchers.Main) {
        isReady.await()
        return@withContext suspendCancellableCoroutine { continuation ->
            val bridge = MercuryBridge { name, payload ->
                when(name) {
                    "success" -> {
                        val content = gson.fromJson(payload, ExtractedContent::class.java)
                        //content, content.extractPlainText.count >= 200  字数大于200
                        continuation.resume(content)
                    }
                    else -> {
                        continuation.resume(null)
                    }
                }
            }
            webView.addJavascriptInterface(bridge, "AndroidBridge")

            val script = """
                (async function() {
                    try {
                        const result = await Mercury.parse($url, {html: $html});
                        window.AndroidBridge.postMessage("success", JSON.stringify(result));
                    } catch(e) {
                        window.AndroidBridge.postMessage("failure", 0);
                    } 
                })();
            """.trimIndent()
            webView.evaluateJavascript(script, null)
        }
    }
}

//class MyApp : Application() {
//    override fun onCreate() {
//        super.onCreate()
//        // 预热单例，加载 WebView 和 JS 库
//        MercuryExtractor.getInstance(this)
//    }
//}