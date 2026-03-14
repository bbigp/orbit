package cn.coolbet.orbit.ui.view.listdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Scaffold
import androidx.compose.ui.unit.IntOffset
import cafe.adriel.voyager.core.screen.Screen
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.ObTheme

/**
 * Generic sticky top-bar layout:
 * - Calculates collapsed state from list scroll.
 * - Measures top-bar height and applies top content inset.
 * - Supports optional overlay below top-bar.
 */
@Composable
fun PinnedTopBarLayout(
    listState: LazyListState,
    compactTitleThreshold: Dp = 60.dp,
    bottomInset: Dp = 0.dp,
    topBar: @Composable (collapsed: Boolean) -> Unit,
    overlay: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val density = LocalDensity.current
    val thresholdPx = with(density) { compactTitleThreshold.roundToPx() }
    var topBarHeightPx by remember { mutableIntStateOf(0) }
    val topInsetDp = with(density) { topBarHeightPx.toDp() }

    val collapsed by remember(listState, thresholdPx) {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) return@derivedStateOf true
            listState.firstVisibleItemScrollOffset > thresholdPx
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        content(PaddingValues(top = topInsetDp, bottom = bottomInset))

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .onSizeChanged { topBarHeightPx = it.height },
            color = ObTheme.colors.primaryContainer
        ) {
            topBar(collapsed)
        }
        AnimatedVisibility(
            visible = overlay != null,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = 0,
                        y = topBarHeightPx
                    )
                }
        ) {
            overlay?.invoke()
        }
    }
}

@Composable
private fun CompactTitle(
    title: String,
    unreadCountText: String,
    showUnreadBadge: Boolean,
    visible: Boolean
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(
                animationSpec = tween(200),
                initialScale = 0.95f
            ),
            exit = fadeOut(animationSpec = tween(200)) + scaleOut(
                animationSpec = tween(200),
                targetScale = 0.95f
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (showUnreadBadge && unreadCountText.isNotBlank()) {
                    Text(
                        text = unreadCountText,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Stable
data class ListDetailReplicaItem(
    val id: Long,
    val text: String
)

object TetScreen: Screen {
    private fun readResolve(): Any = TetScreen

    @Composable
    override fun Content() {
        Scaffold { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                ListDetailStickyTopBarReplicaSample()
            }
        }
    }

}

@Composable
fun ListDetailStickyTopBarReplicaSample(
    title: String = "My List",
    items: List<ListDetailReplicaItem> = List(20) { index ->
        ListDetailReplicaItem(
            id = (index + 1).toLong(),
            text = "List item #${index + 1}"
        )
    },
    isMultiSelectionActive: Boolean = false
) {
    val listState = rememberLazyListState()

    PinnedTopBarLayout(
        listState = listState,
        topBar = { collapsed ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                    if (!isMultiSelectionActive) {
                        Box(modifier = Modifier.weight(1f)) {
                            CompactTitle(
                                title = title,
                                unreadCountText = "99+",
                                showUnreadBadge = true,
                                visible = collapsed
                            )
                        }
                        IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = "Mark all read")
                        }
                        IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Outlined.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Outlined.Build, contentDescription = "More")
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Select Items",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        },
        overlay = {
            Text(
                text = "Tips Placeholder",
                style = MaterialTheme.typography.bodySmall
            )
        }
    ) { contentPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding
        ) {
            item {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
            items(items.size, key = { index -> items[index].id }) { index ->
                Text(
                    text = items[index].text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                )
            }
        }
    }
}
