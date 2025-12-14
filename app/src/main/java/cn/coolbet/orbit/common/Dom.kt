package cn.coolbet.orbit.common

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import kotlin.math.ceil
import kotlin.math.roundToInt

data class HTMLProcessResult(
    val cleanedHTML: String,
    val insertHeroImage: Boolean,
    val hasAudio: Boolean
) {
    companion object {
        val EMPTY = HTMLProcessResult(
            cleanedHTML = "",
            insertHeroImage = false,
            hasAudio = false
        )
    }
}

object HTMLProcessingHelper {

    fun process(
        html: String?,
//        screenWidth: Double,
    ): HTMLProcessResult {

        if (html.isNullOrBlank()) {
            return HTMLProcessResult.EMPTY
        }

        try {
            val doc = Jsoup.parse(html)

//            adjustIframes(doc, screenWidth)
            removeMediaFixedSizes(doc)
            removeInlineStyles(doc)

            val line = estimateLinesUntilFirstImage(doc)

            val hasAudio = doc.select("audio").first() != null

            return HTMLProcessResult(
                cleanedHTML = doc.outerHtml() ?: html,
                insertHeroImage = (line ?: 999) > 10,
                hasAudio = hasAudio
            )

        } catch (error: Exception) {
            return HTMLProcessResult(
                cleanedHTML = html,
                insertHeroImage = false,
                hasAudio = false
            )
        }
    }

    fun estimateLinesUntilFirstImage(doc: Document): Int? {
        val (linesBeforeFirst, found) = doc.body().findFirstImageLines(0)
        return if (found) linesBeforeFirst else null
    }

    private fun adjustIframes(doc: Document, screenWidth: Double) {
        val iframes = doc.select("iframe") ?: return

        for (iframe in iframes) {
            val widthStr = iframe.attr("width")
            val heightStr = iframe.attr("height")

            val width = widthStr.toDoubleOrNull()
            val height = heightStr.toDoubleOrNull()

            if (width == null || height == null || width <= 0) {
                continue
            }

            // 计算新高度
            val newHeight = screenWidth * (height / width)

            iframe.attr("width", screenWidth.roundToInt().toString())
            iframe.attr("height", newHeight.roundToInt().toString())
        }
    }

    private fun removeMediaFixedSizes(doc: Document) {
        val tags = doc.select("img, video, audio") ?: return
        for (tag in tags) {
            tag.removeAttr("width")
            tag.removeAttr("height")
        }
    }

    private fun removeInlineStyles(doc: Document) {
        val nodes = doc.select("[style]") ?: return
        for (node in nodes) {
            node.removeAttr("style")
        }
    }

}

private fun Element.findFirstImageLines(currentLines: Int): Pair<Int, Boolean> {
    var lines = currentLines

    // 遍历当前元素的所有子元素
    for (child in children()) {

        // 我们只对 Element（标签）节点执行行数估算和递归
        if (child !is Element) continue

        // 1. 检查是否是目标图片标签
        if (child.tagName().lowercase() == "img") {
            // 🌟 找到图片！立即返回当前累积的行数，并标记 found = true。
            // (注意：行数在遇到 img 之前累加，所以 img 自己的行数不会被计算)
            return lines to true
        }

        // 2. 累加当前元素的行数 (前序遍历逻辑)
        lines += child.estLineCount

        // 3. 递归遍历子元素
        val (nextLines, found) = child.findFirstImageLines(lines)

        if (found) {
            // 🌟 如果子递归找到了图片，立即将结果向上传递并停止当前循环和函数执行。
            return nextLines to true
        } else {
            // 如果子递归没有找到图片，更新行数累积值，继续下一个兄弟元素的遍历。
            lines = nextLines
        }
    }

    // 遍历完所有子元素，没有找到图片
    return lines to false
}

val Element.estLineCount: Int
    get() {
        val tag = this.tagName().lowercase()
        return when (tag) {
            "video", "embed" -> 5
            "h1", "h2", "h3", "h4", "h5", "h6", "p", "li" -> {
                val charCount = this.text().count()
                if (charCount > 0) {
                    ceil(charCount.toDouble() / 60.0).toInt() + 1
                } else {
                    1 // 至少占据一行
                }
            }
            "tr" -> 1
            else -> 0
        }
    }