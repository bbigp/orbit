//package cn.coolbet.orbit.common
//
//import org.jsoup.Jsoup
//import org.jsoup.nodes.Document
//import org.jsoup.nodes.Element
//import kotlin.math.ceil
//import kotlin.math.roundToInt
//
//// 对应 Swift 中的 HTMLProcessResult
//data class HTMLProcessResult(
//    val cleanedHTML: String,
//    val isFirstImageAfterLimit: Boolean,
//    val hasAudio: Boolean
//) {
//    companion object {
//        val EMPTY = HTMLProcessResult(
//            cleanedHTML = "",
//            isFirstImageAfterLimit = true,
//            hasAudio = false
//        )
//    }
//}
//
//// 对应 Swift 中的 final class HTMLProcessingHelper
//object HTMLProcessingHelper {
//
//    // 对应 Swift 中的 CGFloat，我们使用 Float 或 Double，这里使用 Double
//    fun process(
//        html: String?,
//        screenWidth: Double, // 对应 CGFloat
//        maxLines: Int = 10
//    ): HTMLProcessResult {
//
//        // 对应 guard let html, !html.isBlank else { return .EMPTY }
//        if (html.isNullOrBlank()) {
//            return HTMLProcessResult.EMPTY
//        }
//
//        try {
//            // SwiftSoup.parse(html) -> Jsoup.parse(html)
//            val doc = Jsoup.parse(html)
//
//            adjustIframes(doc, screenWidth)
//            removeMediaFixedSizes(doc)
//            removeInlineStyles(doc)
//
//            val found = detectFirstImageWithinLines(doc, maxLines)
//
//            // doc.select("audio").first() != nil
//            val hasAudio = doc.select("audio").first() != null
//
//            return HTMLProcessResult(
//                cleanedHTML = doc.outerHtml() ?: html,
//                isFirstImageAfterLimit = !found,
//                hasAudio = hasAudio
//            )
//
//        } catch (error: Exception) {
//            // Swift 的 catch 对应 Kotlin 的 catch (error: Exception)
//            return HTMLProcessResult(
//                cleanedHTML = html,
//                isFirstImageAfterLimit = true,
//                hasAudio = false
//            )
//        }
//    }
//
//    private fun detectFirstImageWithinLines(
//        doc: Document,
//        maxLines: Int
//    ): Boolean {
//
//        // 对应 guard let body = doc.body() else { return false }
//        val body = doc.body() ?: return false
//
//        var lineCount = 0
//        var found = false
//
//        // Kotlin 没有 @discardableResult 属性，直接定义递归函数
//        fun dfs(el: Element): Boolean {
//
//            // 检查 img 标签
//            if (el.tagName().equals("img", ignoreCase = true)) {
//                found = true
//                return false
//            }
//
//            // 增加当前节点贡献的行数
//            lineCount += estLineCount(el)
//
//            // 超过限制行数 → 终止
//            if (lineCount > maxLines) {
//                return false
//            }
//
//            // 遍历子节点
//            for (child in el.children()) {
//                // SwiftSoup 遍历 childNodes 时需要 as? Element 过滤非 Element 节点。
//                // Jsoup 的 el.children() 默认只返回 Element 节点，更简洁。
//                if (!dfs(child)) {
//                    return false
//                }
//            }
//            return true
//        }
//
//        // 启动 DFS
//        dfs(body)
//        return found
//    }
//
//    private fun adjustIframes(doc: Document, screenWidth: Double) {
//        // SwiftSoup/Jsoup 的 select 相同
//        val iframes = doc.select("iframe") ?: return
//
//        for (iframe in iframes) {
//            // Jsoup 的 attr() 方法直接返回 String?，Kotlin 中可直接使用 let 结构体
//            val widthStr = iframe.attr("width")
//            val heightStr = iframe.attr("height")
//
//            val width = widthStr.toDoubleOrNull()
//            val height = heightStr.toDoubleOrNull()
//
//            if (width == null || height == null || width <= 0) {
//                continue
//            }
//
//            // 计算新高度
//            val newHeight = screenWidth * (height / width)
//
//            // Jsoup 的 attr() 返回 Element，Kotlin 中不需要 _ = try?
//            iframe.attr("width", screenWidth.roundToInt().toString())
//            iframe.attr("height", newHeight.roundToInt().toString())
//        }
//    }
//
//    private fun removeMediaFixedSizes(doc: Document) {
//        val tags = doc.select("img, video, audio") ?: return
//        for (tag in tags) {
//            // Jsoup 的 removeAttr() 返回 Element
//            tag.removeAttr("width")
//            tag.removeAttr("height")
//        }
//    }
//
//    private fun removeInlineStyles(doc: Document) {
//        val nodes = doc.select("[style]") ?: return
//        for (node in nodes) {
//            node.removeAttr("style")
//        }
//    }
//
//    private fun estLineCount(el: Element): Int {
//        val tag = el.tagName().lowercase() // Swift 的 .lowercased() 对应 Kotlin 的 .lowercase()
//
//        return when (tag) {
//            "video", "embed" -> 5
//
//            "h1", "h2", "h3", "h4", "h5", "h6", "p", "li" -> {
//                // Jsoup 的 text() 返回 String
//                val text = el.text()
//                // Kotlin 的 ceil(Double)
//                val lineEstimate = ceil(text.length.toDouble() / 60.0).toInt()
//                lineEstimate + 1
//            }
//
//            "tr" -> 1
//
//            else -> 0
//        }
//    }
//}