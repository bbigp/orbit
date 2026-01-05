package cn.coolbet.orbit.ui.view.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.common.showTime
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black08

@Composable
fun ArticleMeta(
    entry: Entry,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            entry.title,
            style = AppTypography.M22
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (entry.author.isNotEmpty()) {
                Text(
                    entry.author,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    style = AppTypography.M11B50,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Spacer(modifier = Modifier.width(1.dp).height(6.dp).background(Black08))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                entry.feed.title,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
                style = AppTypography.M11B50,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            entry.publishedAt.showTime(),
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = AppTypography.M11B50
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEntryTile() {
    val entry = Entry.EMPTY.copy(
        title = "Home Assistant’s new voice assistant answers to ‘Hey Jarvis’",
        author = "Jennifer Pattison Tuohy",
        feed = Feed.EMPTY.copy(title = "The Verge"),
        publishedAt = System.currentTimeMillis()
    )
    Column {
        ArticleMeta(entry)
        ArticleMeta(entry.copy(author = "Jennifer Pattison TuohyJennifer Pattison TuohyJennifer Pattison TuohyJennifer Pattison Tuohy"))
        ArticleMeta(entry.copy(feed = Feed.EMPTY.copy(title = "The VergeThe VergeThe VergeThe VergeThe VergeThe VergeThe VergeThe VergeThe Verge")))
        ArticleMeta(entry.copy(author = ""))
    }
}