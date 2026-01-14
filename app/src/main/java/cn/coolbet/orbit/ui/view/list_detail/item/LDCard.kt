package cn.coolbet.orbit.ui.view.list_detail.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.ui.kit.ObAsyncImage
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black08

@Composable
fun LDCard(
    entry: Entry,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White)
            .then(if (entry.isUnread) Modifier else Modifier.alpha(0.5f))
            .then(modifier)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        ObAsyncImage(
            url = entry.leadImageURL,
            modifier = Modifier.padding(horizontal = 16.dp)
                .border(0.5.dp, Black08, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .height(246.dp)
                .fillMaxWidth()
        )
        Text(entry.title, maxLines = 1, style = AppTypography.M15, modifier = Modifier.padding(top = 12.dp))
        LDItemFeedLabel(entry.feed, entry.publishedAt, modifier = Modifier.padding(top = 4.dp))
        Spacer(modifier = Modifier.height(16.dp))
    }
}