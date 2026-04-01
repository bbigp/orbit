package cn.coolbet.orbit.ui.view.content

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebResourceError
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cn.coolbet.orbit.common.HTMLProcessingHelper
import cn.coolbet.orbit.manager.Env
import com.google.gson.Gson
import java.util.UUID

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun ArticleHtml(
    state: ContentState,
    scrollState: ScrollState,
    onDomContentLoaded: (Long) -> Unit = {},
    onArticleRendered: (Long) -> Unit = {},
    onArticleHeightChanged: (Long, Float) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val entry = state.entry
    val initialWebViewHeight = LocalConfiguration.current.screenHeightDp.dp
    val onDomContentLoadedState = rememberUpdatedState(onDomContentLoaded)
    val onArticleRenderedState = rememberUpdatedState(onArticleRendered)
    val onArticleHeightChangedState = rememberUpdatedState(onArticleHeightChanged)
    val entryIdState = rememberUpdatedState(entry.id)

    val fontFamily by Env.settings.articleFontFamily.asState()
    val fontSize by Env.settings.articleFontSize.asState()

    val sourceBody = if (state.isReaderModeEnabled) entry.readableContent else entry.content
    val processedBody = remember(sourceBody, entry.url) {
        HTMLProcessingHelper.prepareArticleHtmlForWebView(
            html = sourceBody,
            refererUrl = entry.url
        )
    }

    val payload = remember(
        state.isReaderModeEnabled, entry.readableContent, entry.content,
        entry.title, entry.author, fontFamily, fontSize, processedBody, entry.url
    ) {
        Gson().toJson(
            ArticlePayload(
                body = HtmlBuilderHelper.htmlBody(processedBody),
                theme = "light",
                head = HtmlBuilderHelper.htmlHead(entry.title, entry.author),
                cssOptionString = HtmlBuilderHelper.rootStyle(fontSize, fontFamily)
            )
        )
    }

    var lastPayload by remember { mutableStateOf("") }
    var webView: WebView? by remember { mutableStateOf(null) }
    var webViewHeight by remember(entry.id) { mutableStateOf(initialWebViewHeight) }
    var isWebViewReady by remember { mutableStateOf(false) }
    val requestOwnerToken = remember { "article-webview-${UUID.randomUUID()}" }

    val webAppInterface = remember {
        WebAppInterface(
            onHeightChange = { height ->
                webViewHeight = height.dp + 30.dp
                onArticleHeightChangedState.value(entryIdState.value, height)
            },
            onEvent = { event ->
                when (event) {
                    "domContentLoadedHandler" -> {
                        onDomContentLoadedState.value(entryIdState.value)
                    }
                    "windowOnloadHandler" -> {
                        isWebViewReady = true
                        webView?.let { ArticleWebViewPool.setTemplateLoaded(it, true) }
                    }
                    "articleRenderedHandler" -> {
                        val targetWebView = webView
                        if (targetWebView == null) {
                            onArticleRenderedState.value(entryIdState.value)
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val requestId = SystemClock.elapsedRealtime()
                            targetWebView.postVisualStateCallback(
                                requestId,
                                object : WebView.VisualStateCallback() {
                                    override fun onComplete(requestId: Long) {
                                        onArticleRenderedState.value(entryIdState.value)
                                    }
                                }
                            )
                            targetWebView.postInvalidateOnAnimation()
                        } else {
                            targetWebView.post { onArticleRenderedState.value(entryIdState.value) }
                        }
                    }
                }
            }
        )
    }

    LaunchedEffect(payload, isWebViewReady) {
        if (isWebViewReady && payload.isNotEmpty() && payload != lastPayload) {
            ArticleImagePipeline.cancelAll(requestOwnerToken)
            webView?.evaluateJavascript("__brewRenderArticle($payload)") {
                lastPayload = payload
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ArticleImagePipeline.cancelAll(requestOwnerToken)
            webView?.let { view ->
                view.evaluateJavascript("window.__brewResetState && window.__brewResetState();", null)
                ArticleWebViewPool.release(view)
                webView = null
            }
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(webViewHeight),
        factory = {
            ArticleWebViewPool.acquire(context).apply {
                webView = this
                setBackgroundColor(Color.Transparent.toArgb())
                removeJavascriptInterface(JS_BRIDGE_NAME)
                addJavascriptInterface(webAppInterface, JS_BRIDGE_NAME)
                webViewClient = object : WebViewClient() {
                    private fun reloadTemplate() {
                        isWebViewReady = false
                        ArticleWebViewPool.setTemplateLoaded(this@apply, false)
                        this@apply.loadDataWithBaseURL(
                            "file:///android_asset/",
                            HtmlBuilderHelper.html(),
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isWebViewReady = true
                        ArticleWebViewPool.setTemplateLoaded(this@apply, true)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        if (request?.isForMainFrame == true) {
                            reloadTemplate()
                        }
                    }

                    override fun onRenderProcessGone(
                        view: WebView?,
                        detail: RenderProcessGoneDetail?
                    ): Boolean {
                        reloadTemplate()
                        return true
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val url = request?.url ?: return null
                        val urlString = url.toString()
                        if (urlString.startsWith("file://")) return null

                        if (!urlString.startsWith("brew://image?")) {
                            return super.shouldInterceptRequest(view, request)
                        }
                        return ArticleImagePipeline.loadImageFromBrewUrl(
                            context = context.applicationContext,
                            ownerToken = requestOwnerToken,
                            brewUrl = urlString,
                            requestHeaders = request.requestHeaders
                        ) ?: run {
                            Log.w("WebView", "image intercept fallback: $urlString")
                            super.shouldInterceptRequest(view, request)
                        }
                    }
                }

                val templateReady = ArticleWebViewPool.isTemplateLoaded(this) && !isBlankWebView(this)
                isWebViewReady = templateReady
                if (!templateReady) {
                    ArticleWebViewPool.setTemplateLoaded(this, false)
                    loadDataWithBaseURL("file:///android_asset/", HtmlBuilderHelper.html(), "text/html", "UTF-8", null)
                } else if (payload.isNotEmpty() && payload != lastPayload) {
                    ArticleImagePipeline.cancelAll(requestOwnerToken)
                    evaluateJavascript("__brewRenderArticle($payload)") {
                        lastPayload = payload
                    }
                }
            }
        },
        update = { target ->
            if (scrollState.value > 40) {
                target.scrollTo(0, 0)
            }
        }
    )
}

private fun isBlankWebView(webView: WebView): Boolean {
    val url = webView.url ?: return true
    return url == "about:blank"
}

class WebAppInterface(
    private val onHeightChange: (Float) -> Unit,
    private val onEvent: (String) -> Unit
) {
    @JavascriptInterface
    fun postMessage(name: String, payload: String) {
        Handler(Looper.getMainLooper()).post {
            when (name) {
                "articleHeightHandler" -> onHeightChange(payload.toFloatOrNull() ?: 0f)
                else -> {
                    onEvent(name)
                    Log.d("WebViewBridge", "Event received: $name, data: $payload")
                }
            }
        }
    }
}
