package cn.coolbet.orbit.ui.view.entry

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import cn.coolbet.orbit.model.domain.Entry


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EntryContent(entry: Entry){
    val context = LocalContext.current
    val fullHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />
            <link rel="stylesheet" type="text/css" href="file:///android_asset/css/styles.css">
        </head>
        <body>
            <h1>本地样式和字体测试</h1>
            <p>${entry.content}</p>
        </body>
        </html>
    """

    AndroidView(
        modifier = Modifier,
        factory = {
            WebView(context).apply {
                // 配置 WebView 布局参数
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
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