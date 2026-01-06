package cn.coolbet.orbit.common

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import org.jsoup.Jsoup


fun String.splitHtml(limit: Int = 250): List<AnnotatedString> {
    val doc = Jsoup.parse(this)
    val segments = mutableListOf<AnnotatedString>()
    var currentLength = 0


    for (element in doc.select("p, div, br")) {
        val plainText = element.text()
        if (plainText.isBlank()) continue

        // 计算还能容纳多少字
        val remaining = limit - currentLength
        if (remaining <= 0) break

        val textToProcess = if (plainText.length > remaining) {
            plainText.take(remaining) + "..."
        } else {
            plainText
        }

        // 这里使用一个简单的转换器，把 HTML 样式转成 AnnotatedString
        segments.add(htmlToAnnotatedString(element.outerHtml(), textToProcess))

        currentLength += textToProcess.length
        if (currentLength >= limit) break
    }
    return segments
}

// 简易 HTML 样式转 AnnotatedString
private fun htmlToAnnotatedString(htmlChunk: String, visibleText: String): AnnotatedString {
    return buildAnnotatedString {
        // 这里为了简单演示，直接添加文字。
        // 进阶做法可以使用原生的 Html.fromHtml(htmlChunk) 得到 Spanned，
        // 然后遍历 Spanned 的各种 Span 并映射为 SpanStyle。
        append(visibleText)

        // 示例：如果包含某个标签，就加粗（实际应用中需用正则或遍历 Span）
//        if (htmlChunk.contains("<b>") || htmlChunk.contains("<strong>")) {
//            addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, visibleText.length)
//        }
    }
}