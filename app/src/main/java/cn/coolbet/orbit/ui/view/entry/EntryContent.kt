package cn.coolbet.orbit.ui.view.entry

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.JsPromptResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cn.coolbet.orbit.model.domain.Entry


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EntryContent(entry: Entry){
    val context = LocalContext.current
    val content = entry.readableContent.ifEmpty { entry.content }
    val fullHtml = """
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
    """

    var webView: WebView? by remember { mutableStateOf(null) }
    var webViewHeight by remember { mutableStateOf(1.dp) }

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
                webViewClient = object : WebViewClient() {
                    @SuppressLint("LocalContextResourcesRead")
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.i("EntryContent", "onPageFinished")
                        view?.evaluateJavascript(
                            "(function() { return document.documentElement.scrollHeight; })();"
                        ) { result ->
                            // 🌟 结果在这里异步返回
                            Log.i("EntryContent", "JS Result: $result")

                            try {
                                // result 是一个 JSON 字符串，包含返回的数字（例如："1234"）
                                // 需要移除可能的引号并转换为浮点数/整数
                                val pxHeight = result.toFloat().toInt()

                                // 转换为 Compose 密度无关像素 (dp)
                                val density = context.resources.displayMetrics.density
                                webViewHeight = (pxHeight / density).dp

                            } catch (e: Exception) {
                                Log.e("WebViewHeight", "Failed to parse height: $result", e)
                                // 如果解析失败，可以设置一个默认高度
                                webViewHeight = 200.dp
                            }
                        }
                    }
                }
                // 2. 加载本地 HTML 内容
                // loadDataWithBaseURL 允许我们指定一个 base URL (file:///android_asset/)
                // 这样 WebView 就能找到 CSS/字体文件。
                loadDataWithBaseURL(
                    "file:///android_asset/", // Base URL for resolving relative paths (e.g., in CSS)
                    fullHtml,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        update = { webView ->

        }
    )
}