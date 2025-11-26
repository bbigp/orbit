package cn.coolbet.orbit.ui.view.search_entries

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography

@Composable
fun NoSearch(hint: String = "Search") {
    Box(
        modifier = Modifier.padding(vertical = 120.dp).fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(56.dp),
                painter = painterResource(id = R.drawable.search_1),
                contentDescription = "",
                contentScale = ContentScale.Fit,
            )
            Text(hint, maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.M15B25)
        }
    }
}