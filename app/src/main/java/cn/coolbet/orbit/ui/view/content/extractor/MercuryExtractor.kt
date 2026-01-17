package cn.coolbet.orbit.ui.view.content.extractor

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.coolbet.orbit.common.asJsString
import cn.coolbet.orbit.common.readAssetText
import com.google.gson.Gson
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
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
    private var activeContinuation: CancellableContinuation<ExtractedContent?>? = null
    private val mutex = Mutex()
    private val bridge = MercuryBridge { name, payload ->
        when(name) {
            "success" -> {
                Log.i("Oeeeed", "Successfully extracted")
                val content = runCatching { gson.fromJson(payload, ExtractedContent::class.java) }.getOrNull()
                //content, content.extractPlainText.count >= 200  字数大于200
                activeContinuation?.resume(content)

            }
            else -> {
                Log.i("Oeeeed", "Failed to extract")
                activeContinuation?.resume(null)
            }
        }
        activeContinuation = null
    }

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
        Log.i("Oeeeed", "Initializing...")
        val mercuryJs = context.readAssetText("js/mercury.web.js")
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.addJavascriptInterface(bridge, "AndroidBridge")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!isReady.isCompleted) {
                    Log.i("Oeeeed", "Ready")
                    isReady.complete(Unit)
                }
            }
        }

//        <script type="text/javascript" src="file:///android_asset/js/mercury.web.js"></script>
        val html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
            <script>$mercuryJs</script>
            </head>
            <body>
                <script>alert('ok')</script>
            </body>
            </html>
        """.trimIndent()
        webView.loadDataWithBaseURL("file:///android_asset/",html, "text/html", "UTF-8", null)
        Log.i("oeeeed", "warmup---")
    }


    suspend fun extract(url: String, html: String): ExtractedContent? {
        return mutex.withLock {
            try {
                withTimeoutOrNull(15000) {
                    isReady.await()
                    suspendCancellableCoroutine { continuation ->
                        activeContinuation = continuation

                        val script = """
                            (async function() {
                                try {
                                    const result = await Mercury.parse(${url.asJsString}, {html: ${html.asJsString}});
                                    console.log(result);
                                    window.AndroidBridge.postMessage("success", JSON.stringify(result));
                                } catch(e) {
                                    console.log(e);
                                    window.AndroidBridge.postMessage("failure", 0);
                                } 
                            })();
                        """.trimIndent()
                        webView.evaluateJavascript(script, null)
                        continuation.invokeOnCancellation {
                            activeContinuation = null
                        }
                    }
                }
            } finally {
                activeContinuation = null
            }

        }
    }
}
