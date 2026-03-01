package cn.coolbet.orbit.ui.view.addfeed

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AddFeedScreenModel(val state: AddFeedState) : ScreenModel {

    private val _unit = MutableStateFlow(AddFeedResultUnit())
    val unit = _unit.asStateFlow()

    fun clearPreview() {
        _unit.value = AddFeedResultUnit()
    }

    
    fun fetchPreview(url: String) {
        clearPreview()
        val normalized = url.trim()
        if (normalized.isBlank()) return
        screenModelScope.launch {
            state.setLoading(true)
            _unit.value = AddFeedResultUnit()
            try {
                val result = withContext(Dispatchers.IO) {
                    loadPreview(normalized)
                }
                _unit.value = result
            } catch (e: Exception) {
                _unit.value = AddFeedResultUnit(error = e.message ?: "Failed to load feed preview")
            } finally {
                state.setLoading(false)
            }
        }
    }

    fun addFeed() {
        // TODO: Implement add feed logic
    }

    
    private fun loadPreview(url: String): AddFeedResultUnit {
        val doc = Jsoup.connect(url)
            .ignoreContentType(true)
            .userAgent("Orbit/1.0")
            .timeout(10000)
            .parser(Parser.xmlParser())
            .get()

        val hasFeedMarkers = doc.selectFirst("rss, feed, channel") != null
        if (!hasFeedMarkers) {
            throw IllegalArgumentException("Not a valid RSS/Atom feed")
        }
        val title = doc.selectFirst("channel > title")?.text()?.trim()
            ?: doc.selectFirst("feed > title")?.text()?.trim()
            ?: ""
        if (title.isBlank()) {
            throw IllegalArgumentException("Not a valid RSS/Atom feed")
        }

        val iconUrl = doc.selectFirst("channel > image > url")?.text()?.trim()
            ?: doc.selectFirst("feed > icon")?.text()?.trim()
            ?: doc.selectFirst("feed > logo")?.text()?.trim()
            ?: ""
        val entries = parseEntries(doc, title, url, iconUrl)

        return AddFeedResultUnit(
            previews = listOf(
                AddFeedPreview(
                    title = title,
                    url = url,
                    iconUrl = iconUrl,
                    entries = entries,
                )
            )
        )
    }

    
    private fun parseEntries(doc: org.jsoup.nodes.Document, title: String, url: String, iconUrl: String): List<Entry> {
        val feed = Feed.EMPTY.copy(
            title = title,
            feedURL = url,
            iconURL = iconUrl,
        )
        val rssItems = doc.select("channel > item")
        if (rssItems.isNotEmpty()) {
            return rssItems.mapIndexed { index, item ->
                val title = item.selectFirst("title")?.text()?.trim().orEmpty()
                val link = item.selectFirst("link")?.text()?.trim().orEmpty()
                val summary = item.selectFirst("description")?.text()?.trim()
                    ?: item.selectFirst("content|encoded")?.text()?.trim().orEmpty()
                val pubDate = item.selectFirst("pubDate")?.text()?.trim().orEmpty()
                Entry(
                    id = (index + 1).toLong(),
                    userId = 0,
                    feedId = 0,
                    title = title.ifEmpty { link },
                    url = link,
                    publishedAt = parseDateToMillis(pubDate),
                    summary = summary,
                    feed = feed,
                )
            }
        }

        val atomEntries = doc.select("feed > entry")
        return atomEntries.mapIndexed { index, entry ->
            val title = entry.selectFirst("title")?.text()?.trim().orEmpty()
            val linkEl = entry.selectFirst("link[rel=alternate]") ?: entry.selectFirst("link")
            val link = linkEl?.attr("href")?.trim()?.ifEmpty { linkEl.text().trim() }.orEmpty()
            val summary = entry.selectFirst("summary")?.text()?.trim()
                ?: entry.selectFirst("content")?.text()?.trim().orEmpty()
            val published = entry.selectFirst("published")?.text()?.trim().orEmpty()
            val updated = entry.selectFirst("updated")?.text()?.trim().orEmpty()
            Entry(
                id = (index + 1).toLong(),
                userId = 0,
                feedId = 0,
                title = title.ifEmpty { link },
                url = link,
                publishedAt = parseDateToMillis(published.ifEmpty { updated }),
                summary = summary,
                feed = feed,
            )
        }
    }

    
    private fun parseDateToMillis(raw: String): Long {
        if (raw.isBlank()) return 0
        val patterns = listOf(
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
        )
        for (pattern in patterns) {
            try {
                val format = SimpleDateFormat(pattern, Locale.US)
                format.timeZone = TimeZone.getTimeZone("UTC")
                val date = format.parse(raw)
                if (date != null) return date.time
            } catch (_: Exception) {
            }
        }
        return 0
    }
}
