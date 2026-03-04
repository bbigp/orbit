package cn.coolbet.orbit.ui.view.addfeed

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.coolbet.orbit.manager.CacheStore
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.manager.Env
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import cn.coolbet.orbit.manager.EntryManager
import cn.coolbet.orbit.manager.FeedManager
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AddFeedScreenModel(
    val state: AddFeedState,
    private val cacheStore: CacheStore,
    private val entryManager: EntryManager,
    private val feedManager: FeedManager,
) : ScreenModel {

    private val _unit = MutableStateFlow(AddFeedResultUnit())
    val unit = _unit.asStateFlow()
    private val _effects = MutableSharedFlow<AddFeedEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

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
                val localFeed = cacheStore.feedsState.value.firstOrNull { it.feedURL == normalized }
                if (localFeed != null) {
                    val localEntries = withContext(Dispatchers.IO) {
                        entryManager.getPage(meta = localFeed, page = 1, size = 20)
                    }
                    _unit.value = AddFeedResultUnit(
                        previews = listOf(
                            AddFeedPreview(
                                title = localFeed.title,
                                feedId = localFeed.id,
                                url = localFeed.feedURL,
                                iconUrl = localFeed.iconURL,
                                subscribeState = AddFeedSubscribeState.SUBSCRIBED,
                                entries = localEntries,
                            )
                        )
                    )
                    return@launch
                }
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

    fun addFeed(preview: AddFeedPreview) {
        if (preview.subscribeState != AddFeedSubscribeState.NOT_SUBSCRIBED) return
        screenModelScope.launch {
            updatePreviewState(preview.url, AddFeedSubscribeState.SUBSCRIBING)
            try {
                val folderId = resolveSubscribeFolderId()
                val feedId = feedManager.subscribeFeed(preview.url, folderId)
                val current = _unit.value
                val next = current.previews.map { item ->
                    if (item.url == preview.url) {
                        item.copy(
                            feedId = feedId,
                            subscribeState = AddFeedSubscribeState.SUBSCRIBED
                        )
                    } else {
                        item
                    }
                }
                _unit.value = current.copy(previews = next, error = null)
            } catch (e: Exception) {
                updatePreviewState(preview.url, AddFeedSubscribeState.NOT_SUBSCRIBED)
                _effects.tryEmit(AddFeedEffect.Error(e.message ?: "Failed to subscribe feed"))
                return@launch
            }
        }
    }

    fun unsubscribeFeed(preview: AddFeedPreview) {
        if (preview.subscribeState != AddFeedSubscribeState.SUBSCRIBED) return
        screenModelScope.launch {
            updatePreviewState(preview.url, AddFeedSubscribeState.SUBSCRIBING)
            try {
                feedManager.unsubscribeFeed(preview.feedId)
                val current = _unit.value
                val next = current.previews.map { item ->
                    if (item.url == preview.url) {
                        item.copy(feedId = 0L, subscribeState = AddFeedSubscribeState.NOT_SUBSCRIBED)
                    } else {
                        item
                    }
                }
                _unit.value = current.copy(previews = next, error = null)
            } catch (e: Exception) {
                updatePreviewState(preview.url, AddFeedSubscribeState.SUBSCRIBED)
                _effects.tryEmit(AddFeedEffect.Error(e.message ?: "Failed to unsubscribe feed"))
                return@launch
            }
        }
    }

    private fun updatePreviewState(url: String, state: AddFeedSubscribeState) {
        val current = _unit.value
        val next = current.previews.map { preview ->
            if (preview.url == url) preview.copy(subscribeState = state) else preview
        }
        _unit.value = current.copy(previews = next, error = null)
    }

    private fun resolveSubscribeFolderId(): Long {
        val folders = cacheStore.foldersState.value
        val rootFolderId = Env.settings.rootFolder.value
        val categoryId = folders.find { it.id == rootFolderId }?.id
            ?: folders.firstOrNull()?.id
            ?: 0L
        require(categoryId > 0L) { "No available folder for subscription" }
        return categoryId
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
                    feedId = 0,
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
