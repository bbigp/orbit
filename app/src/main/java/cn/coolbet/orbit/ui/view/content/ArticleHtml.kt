package cn.coolbet.orbit.ui.view.content

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cn.coolbet.orbit.manager.Env
import com.google.gson.Gson


@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun ArticleHtml(state: ContentState, scrollState: ScrollState){
    val context = LocalContext.current
    val entry = state.entry

    val fontFamily by Env.settings.articleFontFamily.asState()
    val fontSize by Env.settings.articleFontSize.asState()

    val payload = remember(
        state.readerModeOpened, entry.readableContent, entry.content,
        entry.title, entry.author,
        fontFamily, fontSize
    ) {
        Gson().toJson(ArticlePayload(
            body = HtmlBuilderHelper.htmlBody(if (state.readerModeOpened) entry.readableContent else entry.content),
            theme = "light",
            head = HtmlBuilderHelper.htmlHead(entry.title, entry.author),
            cssOptionString = HtmlBuilderHelper.rootStyle(fontSize, fontFamily)
        ))
    }

    var lastPayload by remember { mutableStateOf("") }
    var webView: WebView? by remember { mutableStateOf(null) }
    var webViewHeight by remember { mutableStateOf(1.dp) }
    var isWebViewReady by remember { mutableStateOf(false) }

    val webAppInterface = remember {
        WebAppInterface(
            onHeightChange = { height ->
                webViewHeight = height.dp + 30.dp
            },
            onEvent = { event ->
                when(event) {
                    "windowOnloadHandler" -> { isWebViewReady = true }
                    else -> {}
                }
            }
        )
    }

    LaunchedEffect(payload, isWebViewReady) {
        if (isWebViewReady && payload.isNotEmpty() && payload != lastPayload) {
            webView?.evaluateJavascript("__brewRenderArticle($payload)") {
                lastPayload = payload
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView?.let { view ->
                // 彻底清理和销毁的步骤 (停止加载、移除接口、移除View、loadUrl("about:blank"), destroy())
                view.stopLoading() // 停止任何正在进行的加载
                view.removeJavascriptInterface("Android")
                view.onPause()
                (view.parent as? ViewGroup)?.removeView(view) // 3. 将其从父视图中移除，立即断开其与 View 树的连接
                view.destroy() // 销毁 WebView 实例 (这是防止崩溃最关键的一步)
                webView = null
                Log.d("ContentView", "WebView instance destroyed successfully.")
            }
        }
    }


    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(webViewHeight),
        factory = {
            WebView(context).apply {
                webView = this
                this.setBackgroundColor(Color.Transparent.toArgb())
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
                addJavascriptInterface(webAppInterface, "AndroidBridge")
                loadDataWithBaseURL("file:///android_asset/", HtmlBuilderHelper.html(), "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            if (scrollState.value > 40) { // 强制 WebView 视图对齐到顶部 (清除任何残留的微小负位移)
                webView.scrollTo(0, 0)
            }
        }
    )
}


class WebAppInterface(
    private val onHeightChange: (Float) -> Unit,
    private val onEvent: (String) -> Unit
) {
    @JavascriptInterface
    fun postMessage(name: String, payload: String) {
        Handler(Looper.getMainLooper()).post {
            when (name) {
                "articleHeightHandler" -> {
                    onHeightChange(payload.toFloatOrNull() ?: 0f)
                }
                else -> {
                    onEvent(name)
                    Log.d("WebViewBridge", "Event received: $name, data: $payload")
                }
            }
        }
    }
}