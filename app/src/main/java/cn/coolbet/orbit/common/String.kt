package cn.coolbet.orbit.common

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.core.text.HtmlCompat
import cn.coolbet.orbit.ui.theme.Blue
import com.google.gson.Gson
import org.jsoup.Jsoup



val String.asJsString: String
    get() = Gson().toJson(this)


/**
 * 判断是否为包含样式的 HTML 片段
 */
fun String.hasHtmlTags(): Boolean {
    // 1. 去掉首尾空白符，判空
    val text = this.trim()
    if (text.length < 3) return false // HTML 标签至少需要 3 个字符，如 <a>

    // 2. 检查开头：必须以 '<' 开头
    val startsWithBracket = text.startsWith("<")

    // 3. 检查开头第二个字符：必须是字母或斜杠 (针对闭合标签开头的情况)
    // 这一步有效排除了 <123>、< 5 或文字开头的情况
    val secondChar = text.getOrNull(1)
    val isValidTagStart = secondChar != null && (secondChar.isLetter() || secondChar == '/')

    // 4. 检查结尾：必须以 '>' 结尾
    val endsWithBracket = text.endsWith(">")

    // 只有全部满足，才判定为被 HTML 标签包裹的内容
    return startsWithBracket && isValidTagStart && endsWithBracket
}

fun String.splitHtml(limit: Int = 240): List<AnnotatedString> {
    val doc = Jsoup.parse(this)
    val segments = mutableListOf<AnnotatedString>()
    var currentLength = 0

    val elements = doc.body().children()

    for (element in elements) {
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

private fun htmlToAnnotatedString(htmlChunk: String, visibleText: String): AnnotatedString {
    val spanned = HtmlCompat.fromHtml(htmlChunk, HtmlCompat.FROM_HTML_MODE_LEGACY)

    return buildAnnotatedString {
        append(visibleText)
        val maxLen = visibleText.length

        // 1. 处理所有 URLSpan (a 标签)
        // 关键点：不管 a 标签内部嵌套了 ins, b 还是 span，我们统一给这个区间上色
        val urlSpans = spanned.getSpans(0, spanned.length, android.text.style.URLSpan::class.java)
        urlSpans.forEach { span ->
            val start = spanned.getSpanStart(span).coerceIn(0, maxLen)
            val end = spanned.getSpanEnd(span).coerceIn(0, maxLen)

            if (start < end) {
                // 强制应用蓝色样式，优先级高于其他普通标签
                addStyle(
                    style = SpanStyle(
                        color = Blue,
                    ),
                    start = start,
                    end = end
                )
                // 写入点击注解
                addStringAnnotation(
                    tag = "URL",
                    annotation = span.url,
                    start = start,
                    end = end
                )
            }
        }

        // 2. 处理其他样式（如加粗）
//        val styleSpans = spanned.getSpans(0, spanned.length, android.text.style.StyleSpan::class.java)
//        styleSpans.forEach { span ->
//            val start = spanned.getSpanStart(span).coerceIn(0, maxLen)
//            val end = spanned.getSpanEnd(span).coerceIn(0, maxLen)
//            if (start < end && span.style == android.graphics.Typeface.BOLD) {
//                addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
//            }
//        }
    }
}