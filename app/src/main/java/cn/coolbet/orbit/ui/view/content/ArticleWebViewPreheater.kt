package cn.coolbet.orbit.ui.view.content

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.concurrent.atomic.AtomicBoolean

object ArticleWebViewPreheater {

    @Volatile
    private var started = false

    fun preload(context: Context, count: Int = 2) {
        if (started) return
        started = true

        val appContext = context.applicationContext
        Handler(Looper.getMainLooper()).post {
            repeat(count.coerceAtLeast(1)) {
                warmOne(appContext)
            }
        }
    }

    private fun warmOne(context: Context) {
        val webView = ArticleWebViewPool.acquire(context)
        val completed = AtomicBoolean(false)
        val timeoutRunnable = Runnable {
            finishWarmup(webView, completed)
        }
        Handler(Looper.getMainLooper()).postDelayed(timeoutRunnable, 5600L)

        val bridge = WarmupBridge {
            finishWarmup(webView, completed)
        }

        webView.removeJavascriptInterface(JS_BRIDGE_NAME)
        webView.addJavascriptInterface(bridge, JS_BRIDGE_NAME)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                ArticleWebViewPool.setTemplateLoaded(webView, true)
                webView.evaluateJavascript("window.__setupBrewWarmup && window.__setupBrewWarmup();", null)
            }
        }

        val templateReady = ArticleWebViewPool.isTemplateLoaded(webView) && !isBlank(webView)
        if (templateReady) {
            webView.evaluateJavascript("window.__setupBrewWarmup && window.__setupBrewWarmup();", null)
        } else {
            webView.loadDataWithBaseURL("file:///android_asset/", HtmlBuilderHelper.html(), "text/html", "UTF-8", null)
        }
    }

    private fun finishWarmup(webView: WebView, completed: AtomicBoolean) {
        if (!completed.compareAndSet(false, true)) return
        ArticleWebViewPool.setTemplateLoaded(webView, true)
        ArticleWebViewPool.release(webView)
    }

    private fun isBlank(webView: WebView): Boolean {
        val url = webView.url ?: return true
        return url == "about:blank"
    }
}

private class WarmupBridge(
    private val onReady: () -> Unit
) {
    @JavascriptInterface
    fun postMessage(name: String, payload: String) {
        if (name == "warmupReadyHandler") {
            Handler(Looper.getMainLooper()).post { onReady() }
        }
    }
}
