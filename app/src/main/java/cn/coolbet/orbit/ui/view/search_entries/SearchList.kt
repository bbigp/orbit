package cn.coolbet.orbit.ui.view.search_entries

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.kit.NoMoreIndicator
import cn.coolbet.orbit.ui.kit.SpacerDivider
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black25
import cn.coolbet.orbit.ui.theme.Black50

@Composable
fun SearchList(
    words: Set<String> = emptySet(),
    onClick: (String) -> Unit = {},
    deleteSearchList: () -> Unit  = {},
) {
    val focusManager = LocalFocusManager.current
    Column {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 12.dp, top = 4.dp, bottom = 8.dp)
                .height(36.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "最近搜索",
                maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R13B50,
                modifier = Modifier.padding(bottom = 10.dp).weight(1f)
            )
            Image(
                modifier = Modifier.size(20.dp).clickable(
                    onClick = deleteSearchList
                ),
                painter = painterResource(id = R.drawable.trash),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Black50),
            )
        }

        words.forEach { word ->
            Row(
                modifier = Modifier.height(48.dp).padding(start = 16.dp, end = 12.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            focusManager.clearFocus()
                            onClick(word)
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    word,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, style = AppTypography.R15,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.chevron_right),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Black25),
                )
            }
            SpacerDivider(start = 16.dp, end = 12.dp)
        }
        NoMoreIndicator()

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchList() {
    val words = setOf("少数派", "App", "苹果")
    SearchList(words)
}