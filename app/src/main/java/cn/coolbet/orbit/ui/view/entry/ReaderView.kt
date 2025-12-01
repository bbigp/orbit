package cn.coolbet.orbit.ui.view.entry

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.size
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
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.IOException

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun ReaderView(
    url: String,
    // 🌟 提取结果的回调函数
    onContentExtracted: (ExtractedContent) -> Unit
) {
    val context = LocalContext.current
    val jsContent = remember {
        readAssetFile(context, "js/mercury.web.js")
    }
    val bridge = remember { ContentExtractorBridge(onContentExtracted) }
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


    // 🌟 使用 Modifier.size(0.dp) 或其他方式使其不可见，但不应使用 Modifier.size(0.dp)
    //    因为它可能会阻止 WebView 正确加载和执行脚本。
    //    最可靠的方法是将其放在一个尺寸极小但有效的 Box 中，或依赖于父级判断。

    AndroidView(
        // 关键：将尺寸设为 1x1 像素，使其在视觉上不可见，但能正常运行
        modifier = Modifier.size(1.dp),
        factory = {
            WebView(context).apply {
                webView = this
                layoutParams = ViewGroup.LayoutParams(1, 1) // 确保 View 级别也是 1x1
                settings.javaScriptEnabled = true
                settings.allowFileAccess = false
                settings.domStorageEnabled = true // 启用 DOM Storage

                addJavascriptInterface(bridge, "Android")
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        val extractionScript = """
                            (async function() {
                                const result = await Mercury.parse(window.location.href, {html: document.documentElement.outerHTML});
                                Android.onExtractionComplete(JSON.stringify(result));
                            })();
                        """

                        view?.evaluateJavascript(jsContent) {
                            view.evaluateJavascript(extractionScript) {}
                        }
                    }
                }
                loadUrl(url)
            }
        }
    )
}

class ContentExtractorBridge(private val onContentExtracted: (ExtractedContent) -> Unit) {

    @JavascriptInterface
    fun onExtractionComplete(extractedContent: String) {
        Log.i("readerView", "Bridge $extractedContent")
        val resultObject = gson.fromJson(extractedContent, ExtractedContent::class.java)
        onContentExtracted(resultObject)
    }
}
private val gson = Gson()

data class ExtractedContent(
    val author: String? = "",
    val content: String? = "",
    @SerializedName("date_published") val datePublished: String? = "",
    val domain: String? = "",
    val excerpt: String? = "",
    @SerializedName("lead_image_url") val leadImageUrl: String? = "",
    val title: String? = "",
    val url: String? = ""
)

fun readAssetFile(context: Context, fileName: String): String {
    return try {
        context.assets.open(fileName).use { inputStream ->
            inputStream.bufferedReader().use { it.readText() }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        ""
    }
}