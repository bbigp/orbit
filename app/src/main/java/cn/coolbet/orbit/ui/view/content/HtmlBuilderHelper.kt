package cn.coolbet.orbit.ui.view.content


data class ArticlePayload(
    val head: String? = "",
    val body: String? = "",
    val theme: String? = "light",
    val cssOptionString: String? = ""
)

class HtmlBuilderHelper {

    companion object {

        fun rootStyle(fontSize: Int, fontFamily: String): String {
//            --line-height-multiplier: \(lineHeight);
            return """
                <style>
                :root {
                    --font-size-base: ${fontSize}px;
                    font-size: ${fontSize}px;

                    --font-family-base: ${fontFamily};
                    --font-family-code: ${fontFamily};
                    font-family: ${fontFamily};
                }
                </style>
            """.trimIndent()
        }

        fun htmlHead(title: String, author: String): String {
            return """
                    <title>$title</title>
                    <meta name="author" content="$author">
                    <style>
                    body {
                      padding: 0 16px;
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

        fun htmlBody(content: String): String {
            return """
                    <div id="br-article" class="active">
                      <div class="br-content">$content</div>
                    </div>
                """.trimIndent()
        }

        fun html(theme: String = "light"): String {
            return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />
                    <link rel="stylesheet" type="text/css" href="file:///android_asset/css/main.css">
                    <style id="br-root-style"></style>
                    <script>${htmlJs}</script>
                </head>
                <body data-theme="$theme">
                    <div id="br-article-root"></div>
                </body>
                </html>
            """.trimIndent()
        }


        private val htmlJs: String = """
            (function() {
              // 适配 Android 的消息发送逻辑
              function postMessage(name, payload) {
                try {
                  if (window.AndroidBridge && window.AndroidBridge.postMessage) {
                    // Android 接口统一接收字符串，payload 默认为 0
                    window.AndroidBridge.postMessage(name, JSON.stringify(payload || 0));
                  }
                } catch (error) {
                  console.error('postMessage error', error);
                }
              }
              
              var warmupReadyPosted = false; //预热message是否已经post
              var warmupModeEnabled = false; //预热模式
              var warmupObserverStarted = false; //预热观察器开启
        
              function notifyWarmupReady() {
                if (warmupReadyPosted) { return; }
                warmupReadyPosted = true;
                postMessage('warmupReadyHandler');
              }
        
              //预热加载字体
              function setupWarmupObserver() {
                if (!warmupModeEnabled || warmupObserverStarted) { return; }
                warmupObserverStarted = true;
                function handleReady() {
                  notifyWarmupReady();
                }
                if (document.fonts && document.fonts.ready) {
                  document.fonts.ready.then(handleReady).catch(function() {
                    setTimeout(handleReady, 5000);
                  });
                } else {
                  window.addEventListener('load', function once() {
                    window.removeEventListener('load', once);
                    handleReady();
                  });
                  setTimeout(handleReady, 5000);
                }
              }
        
              window.__setupBrewWarmup = function() {
                warmupModeEnabled = true;
                setupWarmupObserver();
              };
              
              
              function updateRootStyle(styleHTML) {
                if (!styleHTML) { return; }
                try {
                  var parser = document.createElement('div');
                  parser.innerHTML = styleHTML;
                  var newStyle = parser.querySelector('style');
                  var existing = document.getElementById('br-root-style');
                  console.log(parser)
                  console.log(existing)
                  if (newStyle) {
                    newStyle.id = 'br-root-style';
                    if (existing) {
                      existing.replaceWith(newStyle);
                    } else {
                      document.head.appendChild(newStyle);
                    }
                  }
                } catch (error) {
                  console.error('update style error', error);
                }
              }
              
              var previousHeadNodes = [];
              function updateHeadContent(headHTML) {
                previousHeadNodes.forEach(function(node) {
                  if (node.parentNode) {
                    node.parentNode.removeChild(node);
                  }
                });
                previousHeadNodes = [];
        
                if (!headHTML) { return; }
                try {
                  var template = document.createElement('template');
                  template.innerHTML = headHTML;
                  var nodes = Array.from(template.content.childNodes);
                  nodes.forEach(function(node) {
                    var clone = node.cloneNode(true);
                    document.head.appendChild(clone);
                    previousHeadNodes.push(clone);
                  });
                } catch (error) {
                  console.error('update head error', error);
                }
              }
              
              function setInnerHTMLWithScripts(html) {
                var container = document.getElementById('br-article-root');
                if (!container) { return; }
                container.innerHTML = html || '';
                var scripts = container.querySelectorAll('script');
                scripts.forEach(function(script) {
                  var newScript = document.createElement('script');
                  if (script.src) {
                    newScript.src = script.src;
                    newScript.async = false;
                    newScript.onload = function() { newScript.remove(); };
                    newScript.onerror = function() { newScript.remove(); };
                    document.head.appendChild(newScript);
                  } else {
                    newScript.textContent = script.textContent;
                    document.head.appendChild(newScript);
                    newScript.remove();
                  }
                  script.remove();
                });
              }
              
              var articleHeightObserver = null;
              var articleHeightFrame = null;
              var lastArticleHeight = null;
              
              function resetArticleHeightState() {
                if (articleHeightObserver) {
                  articleHeightObserver.disconnect();
                }
                if (articleHeightFrame) {
                  cancelAnimationFrame(articleHeightFrame);
                }
                articleHeightFrame = null;
                lastArticleHeight = null;
              }
        
              function notifyArticleHeight() {
                try {
                  var article = document.getElementById('br-article');
                  if (!article) { return; }
                  var height = article.getBoundingClientRect().height;
                  if (lastArticleHeight === height) { return; }
                  lastArticleHeight = height;
                  postMessage('articleHeightHandler', height);
                } catch (error) {
                  console.error('notifyArticleHeight error', error);
                }
              }
        
              function observeArticleHeight() {
                var article = document.getElementById('br-article');
                if (!article) { return; }
        
                if (!articleHeightObserver) {
                  articleHeightObserver = new ResizeObserver(function(entries) {
                    if (!entries || entries.length === 0) { return; }
                    var nextHeight = entries[0].contentRect.height;
                    if (lastArticleHeight === nextHeight) { return; }
                    if (articleHeightFrame) {
                      cancelAnimationFrame(articleHeightFrame);
                    }
                    articleHeightFrame = requestAnimationFrame(function() {
                      articleHeightFrame = null;
                      lastArticleHeight = nextHeight;
                      postMessage('articleHeightHandler', nextHeight);
                    });
                  });
                } else {
                  articleHeightObserver.disconnect();
                }
        
                articleHeightObserver.observe(article);
                notifyArticleHeight();
              }
              
              window.__brewResetState = function() {
                resetArticleHeightState();
              };
        
              window.__brewRenderArticle = function(payload) {
                console.log(payload)
                if (!payload) { return; }
                resetArticleHeightState();
                updateHeadContent(payload.head);
                if (payload.theme) {
                  document.body.setAttribute('data-theme', payload.theme);
                }
                updateRootStyle(payload.cssOptionString);
                setInnerHTMLWithScripts(payload.body || '');
                observeArticleHeight();
                setupWarmupObserver();
        
                // 这里的 format 函数由外部其他脚本提供
                if (typeof formatBrewPage === 'function') formatBrewPage();
                if (typeof formatAudio === 'function') formatAudio();
        
                requestAnimationFrame(function() {
                  requestAnimationFrame(function() {
                    postMessage('domContentLoadedHandler');
                    postMessage('windowOnloadHandler');
                    postMessage('articleRenderedHandler');
                  });
                });
              };
        
              document.addEventListener('DOMContentLoaded', function() {
                postMessage('domContentLoadedHandler');
              });
        
              window.addEventListener('load', function() {
                postMessage('windowOnloadHandler');
                if (warmupModeEnabled) {
                  notifyWarmupReady();
                }
              });
            })();
            """.trimIndent()
    }
}