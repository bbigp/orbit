package cn.coolbet.orbit.ui.kit

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.view.list_detail.item.pulsatingShimmer
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest


@Composable
fun ObAsyncImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (url.isEmpty()) return
    val context = LocalContext.current
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .httpHeaders(NetworkHeaders.Builder().add("Referer", url).build())
            .build(),
        contentDescription = "",
        contentScale = contentScale,
        modifier = modifier,
        loading = {
            Box(modifier = Modifier.pulsatingShimmer(true))
        },
        error = {
            Image(
                painter = painterResource(R.drawable.no_media),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        },
        success = {
            SubcomposeAsyncImageContent()
        }
    )
}