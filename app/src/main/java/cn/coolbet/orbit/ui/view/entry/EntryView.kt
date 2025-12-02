package cn.coolbet.orbit.ui.view.entry

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObIconTextButton

@Composable
fun EntryView(entry: Entry) {
    val scrollState = rememberScrollState()
    CompositionLocalProvider(
        LocalOverscrollFactory provides null,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            if (entry.pic.isNotEmpty()) {
                EntryImage(entry.pic)
            }
            EntryTitle(entry)
            EntryContent(entry)
            NoMoreIndicator(height = 60.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ObIconTextButton(
                    content = "View Website",
                    icon = R.drawable.out_o,
                    sizes = OButtonDefaults.mediumPadded.copy(horizontalPadding = 32.dp),
                    colors = OButtonDefaults.secondary,
                    iconOnRight = true
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEntryView() {
    val entry = Entry.EMPTY.copy(
        title = "Home Assistant’s new voice assistant answers to ‘Hey Jarvis’",
        author = "Jennifer Pattison Tuohy",
        feed = Feed.EMPTY.copy(title = "The Verge"),
        publishedAt = System.currentTimeMillis()
    )
    EntryView(entry)
}