package cn.coolbet.orbit.ui.view.content

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

object ArticleWebViewPool {

    private const val MAX_POOL_SIZE = 4
    private val EMPTY_CLIENT = WebViewClient()
    private val pool = ArrayDeque<WebView>()
    private val templateLoadedMap = mutableMapOf<Int, Boolean>()

    @Synchronized
    fun acquire(context: Context): WebView {
        val appContext = context.applicationContext
        val webView = if (pool.isNotEmpty()) {
            pool.removeLast()
        } else {
            createWebView(appContext)
        }
        prepareForReuse(webView)
        return webView
    }

    @Synchronized
    fun release(webView: WebView) {
        (webView.parent as? android.view.ViewGroup)?.removeView(webView)
        webView.stopLoading()
        webView.webViewClient = EMPTY_CLIENT
        webView.removeJavascriptInterface(JS_BRIDGE_NAME)
        webView.scrollTo(0, 0)
        webView.alpha = 1f

        if (pool.size < MAX_POOL_SIZE) {
            pool.addLast(webView)
        } else {
            templateLoadedMap.remove(System.identityHashCode(webView))
            webView.destroy()
        }
    }

    @Synchronized
    fun setTemplateLoaded(webView: WebView, loaded: Boolean) {
        templateLoadedMap[System.identityHashCode(webView)] = loaded
    }

    @Synchronized
    fun isTemplateLoaded(webView: WebView): Boolean {
        return templateLoadedMap[System.identityHashCode(webView)] ?: false
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(context: Context): WebView {
        return WebView(context).apply {
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER

            settings.textZoom = 100
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.loadsImagesAutomatically = true
            settings.allowContentAccess = true
            settings.allowFileAccess = true

            templateLoadedMap[System.identityHashCode(this)] = false
        }
    }

    private fun prepareForReuse(webView: WebView) {
        webView.scrollTo(0, 0)
    }
}

const val JS_BRIDGE_NAME = "AndroidBridge"
