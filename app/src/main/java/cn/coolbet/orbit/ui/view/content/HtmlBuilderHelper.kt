package cn.coolbet.orbit.ui.view.content

import androidx.compose.ui.graphics.Color


class ContentSettings(
    val cssFontSize: String = "15",
    val cssFontFamily: String = "DM Sans",
    val lineHeight: Double,
    val fixedColor: Color,
    val dynamicColor: Color
) {
    fun cssOptionString(): String {
//        --line-height-multiplier: $lineHeight;
        return """
                <style id='br-root-style'>
                :root {
                    --font-size-base: ${cssFontSize}px;
                    font-size: ${cssFontSize}px;
    
                    --font-family-base: ${cssFontFamily};
                    --font-family-code: ${cssFontFamily};
                    font-family: ${cssFontFamily};
                }
                </style>
          """.trimIndent()
    }
}

class HtmlBuilderHelper {

    companion object {
        fun html(title: String = "", author: String = "", content: String = "", css: String = "", script: String = "", theme: String = ""): String {
            return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />
                    <link rel="stylesheet" type="text/css" href="file:///android_asset/css/main.css">
                    <title>$title</title>
                    <meta name="author" content="$author">
                    $css
                </head>
                <body data-theme="$theme">
                    <div id="br-article" class="active">
                        <div class="br-content">$content</div>
                    </div>
                </body>
                </html>
            """.trimIndent()
        }

        fun entryHtml(title: String, author: String, content: String): String {
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