package cn.coolbet.orbit.ui.view.entry

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.JsPromptResult
import android.webkit.WebChromeClient
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
    val heightBridge = remember { HeightBridge(onHeight = { height ->
        webViewHeight = height.dp + 20.dp
    }) }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.clearHistory()
            webView?.loadUrl("about:blank") // 推荐：加载空白页
            webView?.onPause() // 推荐：暂停活动
            webView?.destroy()
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
        modifier = Modifier.fillMaxWidth().height(webViewHeight),
        factory = {
            WebView(context).apply {
                webView = this
                this.isVerticalScrollBarEnabled = false
                this.isHorizontalScrollBarEnabled = false
                this.overScrollMode = View.OVER_SCROLL_NEVER
                settings.textZoom = 100
                settings.javaScriptEnabled = true
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

class HeightBridge(private val onHeight: (Int) -> Unit) {

    @JavascriptInterface
    fun onExtractionComplete(height: String) {
        Log.i("EntryContent", "Bridge $height")
        onHeight(height.toFloat().toInt())
    }
}