package cn.coolbet.orbit.ui.view.list_detail.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.common.showTime
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.EntryStatus
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.theme.AppTypography
import java.util.Date


@Composable
fun LDTextOnly(
    entry: Entry,
    modifier: Modifier = Modifier
){

    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White)
            .then(if (entry.isUnread) Modifier else Modifier.alpha(0.5f))
            .then(modifier)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                entry.feed.title,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = AppTypography.M13,
                modifier = Modifier.weight(1f)
            )
            Text(
                entry.publishedAt.showTime(),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = AppTypography.M13B25,
                modifier = Modifier.wrapContentWidth()
            )
        }

        Text(
            entry.title,
            maxLines = 2, overflow = TextOverflow.Ellipsis,
            style = AppTypography.M15,
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )
        if (entry.summary.isNotEmpty()) {
            Text(
                entry.summary,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
                style = AppTypography.R13B50,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLDTextOnly(){
    val entry = Entry.EMPTY.copy(
        title = "The best last-minute Cyber Monday deals you can still shop",
        summary = "There’s still some time to save on a wide range of Verge-approved goods, including streaming services, iPads, and ebook readers.",
        feed = Feed.EMPTY.copy(title = "The Verge"),
        publishedAt = Date().time,
    )
    Column {
        LDTextOnly(entry = entry)
        LDTextOnly(entry = entry.copy(summary = "", status = EntryStatus.READ))
    }
}