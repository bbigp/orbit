package cn.coolbet.orbit.ui.view.entry

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.JsPromptResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import cn.coolbet.orbit.common.openURL
import cn.coolbet.orbit.model.domain.Entry


@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun EntryContent(state: EntryState, scrollState: ScrollState){
    val context = LocalContext.current
    val entry = state.entry
    val fullHtml: String by remember(state.readerView, entry.content, entry.readableContent, entry.title) {
        val content = if (state.readerView) entry.readableContent else entry.content
        mutableStateOf("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />
                <link rel="stylesheet" type="text/css" href="file:///android_asset/css/main.css">
                <title>${entry.title}</title>
            </head>
            <body>
                <div id="br-article" class="active">
                    <div class="br-content">${content}</div>
                </div>
            </body>
            </html>
        """.trimIndent())

    }



    var webView: WebView? by remember { mutableStateOf(null) }
    var webViewHeight by remember { mutableStateOf(1.dp) }
    val heightBridge = remember {
        HeightBridge(onHeight = { height ->
            webViewHeight = height.dp + 30.dp
        }, openURL = { url ->
            openURL(context, url.toUri())
        })
    }

    DisposableEffect(Unit) {
        onDispose {
            webView?.apply {
                stopLoading()
                clearHistory()
                webView?.loadUrl("about:blank") // 推荐：加载空白页
                onPause() // 推荐：暂停活动
                removeJavascriptInterface("Android")
                destroy()
            }
            webView = null
        }
    }

    LaunchedEffect(fullHtml, webView) {
        // 加载本地 HTML 内容
        // loadDataWithBaseURL 允许我们指定一个 base URL (file:///android_asset/)
        // 这样 WebView 就能找到 CSS/字体文件。
        webView?.loadDataWithBaseURL(
            "file:///android_asset/", // Base URL for resolving relative paths (e.g., in CSS)
            fullHtml,
            "text/html",
            "UTF-8",
            null
        )
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(webViewHeight),
        factory = {
            WebView(context).apply {
                webView = this
                this.isVerticalScrollBarEnabled = false
                this.isHorizontalScrollBarEnabled = false
                this.overScrollMode = View.OVER_SCROLL_NEVER
                settings.textZoom = 100
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                settings.loadsImagesAutomatically = true
                settings.allowContentAccess = true
                settings.allowFileAccess = true
                addJavascriptInterface(heightBridge, "Android")
                webViewClient = object : WebViewClient() {
                    @SuppressLint("LocalContextResourcesRead")
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.i("EntryContent", "onPageFinished")
//                        "(function() { return document.documentElement.scrollHeight; })();"
                        view?.evaluateJavascript("""
                            const height = document.getElementById('br-article').getBoundingClientRect().height
                            console.log(height)
                            Android.onExtractionComplete(height);
                        """.trimIndent(), null)

                        view?.evaluateJavascript("""
                            document.body.addEventListener('click', function(event) {
                                // 检查是否点击的是 a 标签
                                let element = event.target.tagName == "A" ? event.target : event.target.closest('a');
                                if (element) {
                                    try {
                                        let url = new URL(element.href);
                                        Android.url(url.href)
                                        event.preventDefault();
                                        event.stopPropagation();
                                    } catch(e) {
                                        Android.error('a-tag-clicked-with-invalid-url')
                                    }
                                } else if (event.target && event.target.tagName == "IMG" && event.target.src) {
                                    let img = event.target;
                                    let rect = img.getBoundingClientRect();
                                    let data = { src: img.src, left: rect.left, top: rect.top, width: rect.width, height: rect.height };
                                    Android.openImg(JSON.stringify(data))
                                } else {
                                    Android.onClick('body-clicked')
                                }
                            }, true);
                        """.trimIndent(), null)
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val response = super.shouldInterceptRequest(view, request)
                        val url = request?.url.toString()
                        if (url.contains(".jpg") || url.contains(".png") || url.contains(".gif")) {
                            Log.d("EntryContent", "Intercepting potential image request: $url ${response?.mimeType}")
                        }
                        return response
                    }
                }
            }
        },
        update = { webView ->
            if (scrollState.value > 40) {
                // 强制 WebView 视图对齐到顶部 (清除任何残留的微小负位移)
                webView.scrollTo(0, 0)
            }
        }
    )
}

class HeightBridge(
    private val onHeight: (Int) -> Unit,
    private val openURL: (String) -> Unit,
) {

    @JavascriptInterface
    fun onExtractionComplete(height: String) {
        Log.i("EntryContent", "Bridge $height")
        onHeight(height.toFloat().toInt())
    }

    @JavascriptInterface
    fun onClick(data: String) {
        Log.i("EntryContent", "onClick: $data")
    }

    @JavascriptInterface
    fun url(url: String) {
        openURL(url)
    }

    @JavascriptInterface
    fun openImg(url: String) {

    }

    @JavascriptInterface
    fun error(msg: String) {
        Log.i("EntryContent", "error: $msg")
    }
}