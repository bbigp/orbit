package cn.coolbet.orbit.ui.view.content

import androidx.compose.ui.graphics.Color


class ContentSettings(
    val cssFontSize: String,
    val cssFontFamily: String,
    val lineHeight: Double,
    val fixedColor: Color,

) {
    fun cssOptionString(): String {
        return """
                <style id='br-root-style'>
                :root {
                    --font-size-base: ${cssFontSize}px;
                    font-size: ${cssFontSize}px;
    
                    --line-height-multiplier: $lineHeight;
    
                    --font-family-base: ${cssFontFamily};
                    --font-family-code: ${cssFontFamily};
                    font-family: ${cssFontFamily};
                }
                </style>
          """.trimIndent()
    }
}

class HtmlData(val head: String, val body: String){

    fun getHtml(beforeHead: String = "", afterBody: String = "", theme: String): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />
                $beforeHead
                $head
            </head>
            <body data-theme="$theme">
                $body
                $afterBody
            </body>
            </html>
        """.trimIndent()
    }
}

class HtmlBuilderHelper {

    companion object {
        fun html(title: String = "", author: String = "", content: String = "", css: String = "", script: String = ""): HtmlData {
            return HtmlData(
                head = """
                <title>$title</title>
                <meta name="author" content="$author">
                $css
            """.trimIndent(),
                body = """
                <div id="br-article" class="active">
                    <div class="br-content">$content</div>
                </div>
                $script
            """.trimIndent()
            )
        }

        fun entryHtml(title: String, author: String, content: String): HtmlData {
            return html(
                title = title, author = author, content = content,
                css = articleCss,
            )
        }

        val articleCss: String = """
            <style>
            body {
              padding: 0 20px;
              margin: 0;
            }
            
            #br-skeleton, #br-article {
               display: none;
            }
            #br-skeleton.active, #br-article.active {
               display: block;
            }
            
            .br-content {
               overflow: hidden;
            }
            .br-content > p:empty {
               display: none;
            }
            .br-content *:first-child {
               margin-top: 0;
            }
            </style>
        """.trimIndent()
    }
}