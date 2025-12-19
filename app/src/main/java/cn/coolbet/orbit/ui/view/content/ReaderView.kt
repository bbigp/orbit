package cn.coolbet.orbit.ui.view.content

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
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
import java.io.IOException

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun ReaderView(
    key: Long,
    url: String,
    // 🌟 提取结果的回调函数
    onContentExtracted: (ExtractedContent, Long) -> Unit
) {
    val context = LocalContext.current
    val jsContent = remember {
        readAssetFile(context, "js/mercury.web.js")
    }
    val bridge = remember { ContentExtractorBridge(onContentExtracted, key) }
    var webView: WebView? by remember { mutableStateOf(null) }

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
                Log.d("ReaderView", "WebView instance destroyed successfully.")
            }
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

                    // 🌟 辅助函数：安全地执行 JS
                    fun safeEvaluateJavascript(
                        script: String,
                        callback: ValueCallback<String>? = null
                    ) {
                        webView?.let { validWebView ->
                            // 确保在主线程执行
                            validWebView.post {
                                // 再次检查引用是否仍然是当前有效的实例
                                if (webView == validWebView) {
                                    validWebView.evaluateJavascript(script, callback)
                                } else {
                                    Log.w("ReaderView", "Skipping JS: WebView reference changed/destroyed during post.")
                                }
                            }
                        }
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        val extractionScript = """
                            (async function() {
                                const result = await Mercury.parse(window.location.href, {html: document.documentElement.outerHTML});
                                Android.onExtractionComplete(JSON.stringify(result));
                            })();
                        """

//                                validWebView.evaluateJavascript(jsContent) {
//                                    validWebView.evaluateJavascript(extractionScript) {}
//                                }

                        // 1. 外部调用：注入第一个JS (jsContent)
                        safeEvaluateJavascript(jsContent) {
                            // 2. 内部回调：在执行第二个JS之前，再次调用安全函数
                            safeEvaluateJavascript(extractionScript) { result ->
                                // 3. 最终回调：处理结果 (这个回调也可能延迟)
                                // 这里的 onExtractionComplete 应该已经通过 bridge 实现了安全检查
                                // ... 处理结果逻辑 ...
                            }
                        }
                    }
                }
                loadUrl(url)
            }
        }
    )
}

class ContentExtractorBridge(
    private val onContentExtracted: (ExtractedContent, Long) -> Unit,
    private val key: Long
) {

    @JavascriptInterface
    fun onExtractionComplete(extractedContent: String) {
        Log.i("readerView", "Bridge extractedContent $extractedContent")
        val resultObject = gson.fromJson(extractedContent, ExtractedContent::class.java)
        onContentExtracted(resultObject, key)
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