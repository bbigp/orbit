package cn.coolbet.orbit.ui.view.list_detail.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.common.showTime
import cn.coolbet.orbit.model.domain.Entry
import cn.coolbet.orbit.model.domain.Feed
import cn.coolbet.orbit.ui.kit.ObAsyncImage
import cn.coolbet.orbit.ui.kit.ObCard
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black04
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.view.FeedIcon
import cn.coolbet.orbit.ui.view.FeedIconDefaults
import java.util.Date


@Composable
fun LDThread(
    entry: Entry,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White)
            .then(if (entry.isUnread) Modifier else Modifier.alpha(0.5f))
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            FeedIcon(
                url = entry.feed.iconURL,
                alt = entry.feed.title,
                size = FeedIconDefaults.LARGE.copy(radius = 99.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            entry.feed.title,
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                            style = AppTypography.M15,
                        )
                        if (entry.author.isNotEmpty()) {
                            Text(entry.author, maxLines = 1, style = AppTypography.M13B50)
                        }
                    }
                    Text(
                        entry.publishedAt.showTime(),
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        style = AppTypography.M13B25,
                        modifier = Modifier.wrapContentWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                entry.segments.forEach { segment ->
                    Text(
                        text = segment,
                        style = AppTypography.R15,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                if (entry.showReadMore) {
                    Text("Read More...", maxLines = 1, style = AppTypography.R15Blue)
                }
                if (entry.leadImageURL.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ObAsyncImage(
                        url = entry.leadImageURL,
                        modifier = Modifier.fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 0.5.dp,
                                color = Black08,
                                shape = RoundedCornerShape(12.dp)
                            )
                    )
                }
//                if(true) {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    OGMetaCard()
//                }
            }
        }
    }
}

data class OGMeta(
    val title: String,
    val desc: String,
    val image: String,
    val siteIcon: String,
    val siteName: String,
    val url: String
)

@Composable
fun OGMetaCard(
    ogMeta: OGMeta = OGMeta(
        siteIcon = "",
        siteName = "Aphex Twin - The Official Store",
        title = "Selected Selected Selected Selected Ambient Works Vol.II",
        desc = "'Selected Ambient Works Volume II (Expanded Edition) ' Available to Pre- Order Now. Buy Vinyl, CD, Digital, Merch.",
        image = "https://img.36krcdn.com/hsossms/20260107/v2_702a6dc5689a40548029bb764b9de86f@6022551_oswg70591oswg1053oswg495_img_jpg?x-oss-process=image/resize,m_mfit,w_600,h_400,limit_0/crop,w_600,h_400,g_center",
        url = "",
    ),
) {
    ObCard(
        radius = 12.dp,
        contentHorizontal = 12.dp,
        contentVertical = 12.dp,
        background = Black04
    ) {
        Column {
            Row {
                FeedIcon(
                    modifier = Modifier.padding(end = 6.dp),
                    url = ogMeta.siteIcon,
                    alt = ogMeta.siteName,
                    size = FeedIconDefaults.SMALL
                )
                Text(ogMeta.siteName, maxLines = 1, style = AppTypography.M13)
            }
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(ogMeta.title, maxLines = 2, style = AppTypography.M15)
                    Text(ogMeta.desc, maxLines = 2, style = AppTypography.R13B50, modifier = Modifier.padding(top = 4.dp))
                }
                if (ogMeta.image.isNotEmpty()) {
                    ObAsyncImage(
                        url = ogMeta.image,
                        modifier = Modifier.width(64.dp)
                            .height(56.dp)
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(
                                width = 0.5.dp,
                                color = Black08,
                                shape = RoundedCornerShape(6.dp)
                            ),
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLDThread() {
    val entry = Entry.EMPTY.copy(
        title = "The best last-minute Cyber Monday deals you can still shop",
        summary = "There’s still some time to save on a wide range of Verge-approved goods, including streaming services, iPads, and ebook readers.",
        feed = Feed.EMPTY.copy(title = "The Verge"),
        publishedAt = Date().time,
        content = "<p>Lenovo has brought a slew of updates to its Legion and LOQ line of gaming laptops for CES 2026.\n" +
                "    <a data-i13n=\"cpos:2;pos:1\" href=\"https://www.engadget.com/computing/ces-2026-intel-hopes-its-core-ultra-series-3-chips-are-the-start-of-a-comeback-000155611.html\">Intel Core Ultra Series 3</a>The refreshed laptops are all built around Nvidia RTX 50-series GPUs.</p>\n" +
                "<p>The new Legion 7a is both thinner and lighter than the previous generation and is aimed at gamers, creators, and working professionals. Lenovo says the new 7a will be powered by <a data-i13n=\"cpos:1;pos:1\" href=\"https://www.engadget.com/computing/amds-ryzen-ai-400-chips-are-a-big-boost-for-laptops-and-desktops-alike-033000635.html\"><ins>AMD Ryzen AI 400 CPUs</ins></a> and RTX 50-series GPUs, delivering up to 125W of total system power.&nbsp;</p>\n" +
                "<span id=\"end-legacy-contents\"></span><p>Presumably this means buyers will choose from multiple CPU and GPU configurations, and Lenovo says the 7a will support up to a Ryzen AI 9 HX 470 and up to an RTX 5060 GPU, but precise details on other configurations have not been made available.</p>\n" +
                "<p>The laptop sports a 16-inch OLED display and Lenovo says the laptop&#39;s &quot;AI-optimized&quot; performance is ready to handle complex coding, simulation, and 3D modeling projects. The 7a runs on Windows 11 Copilot+ and uses on-board software to dynamically tune power use and thermals depending on the workload the laptop is under. The Legion 7a will start at \$2,000, with availability &quot;expected&quot; in April.</p>\n" +
                "<p>Lenovo is also refreshing the Legion 5 line with the Legion 5i powered by the new\n" +
                "    and Legion 5a with a choice of an AMD Ryzen AI 400 or Ryzen 200. Both will offer RTX 50-series GPUs, OLED displays and the same software-based tuning features as the 7a. Lenovo says the 5i can be figured up to an Intel Ultra 9 386H with an RTX 5060 GPU, and the 5a up to a Ryzen AI 9 465 with RTX 5060. Here again we don&#39;t yet have details on alternate configurations.</p>\n" +
                "<p>The Legion 5 laptops run on Windows 11 Copilot+, and Lenovo says they are ready for gaming, streaming, building presentations and video editing. Pricing starts at \$1,550 for the 5i, \$1,500 for the 5a with Ryzen AI 400 and \$1,300 for the 5a with Ryzen 200. Lenovo also expects these laptops to be available in April.</p>\n" +
                "<p>At the entry level, the LOQ 15AHP11 and LOQ 15IPH11 target students with RTX 50-series graphics and a WQXGA (2560 x 1600) LCD display up to 15.3 inches. The 15AHP11 will start from \$1,150 with availability expected in April, while the 15IPH11 will not be sold in the US.</p>This article originally appeared on Engadget at https://www.engadget.com/gaming/pc/lenovo-updates-its-legion-and-loq-gaming-laptops-for-ces-010042509.html?src=rss"


    )
    Column(modifier = Modifier.wrapContentHeight()) {
        LDThread(entry.copy(leadImageURL = "https://img.36krcdn.com/hsossms/20260107/v2_702a6dc5689a40548029bb764b9de86f@6022551_oswg70591oswg1053oswg495_img_jpg?x-oss-process=image/resize,m_mfit,w_600,h_400,limit_0/crop,w_600,h_400,g_center"))
        LDThread(entry.copy(author = "@AphexTwin"))
        LDThread(entry.copy(content = "", summary = ""))
        LDThread(entry.copy(content = "", summary = "那么奇、那么秘，下站极地。<a href=\"https://sspai.com/post/104946\" target=\"_blank\">查看全文</a>"))
    }
}