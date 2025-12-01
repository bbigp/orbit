package cn.coolbet.orbit.ui.view.entry

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
        modifier = Modifier.fillMaxSize(),
        factory = {
            WebView(context).apply {
                webView = this
                // 配置 WebView 布局参数
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.textZoom = 100
                isVerticalScrollBarEnabled = false
                // 启用 JavaScript (如果需要)
                settings.javaScriptEnabled = true
                // 允许访问文件系统，以便加载本地资源
                settings.allowFileAccess = true
                // 确保在应用内部处理页面跳转，而不是打开外部浏览器
                webViewClient = WebViewClient()
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