package cn.coolbet.orbit.ui.view.entry

import android.content.Intent
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.OButtonDefaults
import cn.coolbet.orbit.ui.kit.ObIconTextButton
import androidx.core.net.toUri
import cn.coolbet.orbit.common.openURL

@Composable
fun EntryView(state: EntryState) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val entry = state.entry
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
            EntryContent(state, scrollState)
            NoMoreIndicator(height = 40.dp)
            if (entry.url.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ObIconTextButton(
                        content = "View Website",
                        icon = R.drawable.out_o,
                        sizes = OButtonDefaults.mediumPadded.copy(horizontalPadding = 32.dp),
                        colors = OButtonDefaults.secondary,
                        iconOnRight = true,
                        onClick = { openURL(context, entry.url.toUri()) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
        Box(modifier = Modifier.background(Color.White).wrapContentSize()) {
            Text("${scrollState.value}    ${scrollState.maxValue}")
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
    EntryView(EntryState(entry))
}