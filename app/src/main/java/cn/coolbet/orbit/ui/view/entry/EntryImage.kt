package cn.coolbet.orbit.ui.view.entry

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.view.entries.pulsatingShimmer
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest

@Composable
fun EntryImage(
    url: String,
    alt: String = ""
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .httpHeaders(NetworkHeaders.Builder().add("Referer", url).build())
                .build(),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(241.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 0.5.dp,
                    color = Black08,
                    shape = RoundedCornerShape(16.dp)
                ),
            loading = {
                Box(modifier = Modifier.pulsatingShimmer(true))
            },
            error = {
                Image(
                    painter = painterResource(R.drawable.no_media),
                    contentDescription = null,
                    contentScale = ContentScale.None
                )
            },
            success = {
                SubcomposeAsyncImageContent()
            }
        )
        if (alt.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(alt, style = AppTypography.M11B25)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEntryImage() {
    Column {
        EntryImage("")
        EntryImage("", alt = "The Home Assistant Voice Preview Edition is the first hardware from the open-source smart home platform that’s designed for voice control. Image: Nabu Casa")
    }
}