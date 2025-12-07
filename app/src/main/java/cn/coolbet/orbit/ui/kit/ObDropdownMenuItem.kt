package cn.coolbet.orbit.ui.kit

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MenuDefaults.ShadowElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black95
import cn.coolbet.orbit.ui.theme.ContainerSecondary
import cn.coolbet.orbit.ui.theme.ContentRed

@Composable
fun ObDropdownMenu(
    expandedState: MutableTransitionState<Boolean>,
    onDismissRequest: () -> Unit,
    dpOffset: DpOffset = DpOffset(0.dp, 0.dp),
    content: @Composable () -> Unit
) {

    val transformOriginState = remember { mutableStateOf(TransformOrigin.Center) }

    @Suppress("DEPRECATION") val transition = updateTransition(expandedState, "DropDownMenu")

    val scale by
    transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // Dismissed to expanded
                tween(durationMillis = InTransitionDuration, easing = LinearOutSlowInEasing)
            } else {
                // Expanded to dismissed.
                tween(durationMillis = 1, delayMillis = OutTransitionDuration - 1)
            }
        }
    ) { expanded ->
        if (expanded) ExpandedScaleTarget else ClosedScaleTarget
    }

    val alpha by
    transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // Dismissed to expanded
                tween(durationMillis = 30)
            } else {
                // Expanded to dismissed.
                tween(durationMillis = OutTransitionDuration)
            }
        }
    ) { expanded ->
        if (expanded) ExpandedAlphaTarget else ClosedAlphaTarget
    }
    val isInspecting = LocalInspectionMode.current

    if (expandedState.currentState || expandedState.targetState) {
        val density = LocalDensity.current

        // 1. 创建你的自定义定位提供者
        val positionProvider = remember(density) {
            ObDropdownPositionProvider(
                contentOffset = dpOffset,
                density = density
            )
        }
        if (alpha > 0f) {
            Popup(
                onDismissRequest = onDismissRequest,
                popupPositionProvider = positionProvider,
            ) {
                Surface(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX =
                                if (!isInspecting) scale
                                else if (expandedState.targetState) ExpandedScaleTarget else ClosedScaleTarget
                            scaleY =
                                if (!isInspecting) scale
                                else if (expandedState.targetState) ExpandedScaleTarget else ClosedScaleTarget
                            this.alpha =
                                if (!isInspecting) alpha
                                else if (expandedState.targetState) ExpandedAlphaTarget else ClosedAlphaTarget
                            transformOrigin = transformOriginState.value
                        },
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                ) {
                    Column(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuDivider(
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.width(240.dp).then(modifier)) { SpacerDivider() }
}

@Composable
fun ObDropdownMenuItem(
    modifier: Modifier = Modifier,
    text: String = "",
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    colors: ObMenuItemColors = ObMenuDefaults.defaultColors,
    onClick: () -> Unit = {}
){
    val defaultModifier = Modifier.height(44.dp)
        .wrapContentWidth().widthIn(max = 240.dp)
        .padding(all = 0.dp)
        .clickable(
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        )
    Row(
        modifier = defaultModifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(8.dp))

        if (leadingIcon != null) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = leadingIcon),
                contentDescription = "",
                contentScale = ContentScale.None,
                colorFilter = ColorFilter.tint(colors.color),
            )
        } else {
            Spacer(modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AppTypography.R15.copy(color = colors.color),
            modifier = if (trailingIcon != null) Modifier.weight(1f) else Modifier
        )
        Spacer(modifier = Modifier.width(8.dp))

        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = trailingIcon),
                contentDescription = "",
                contentScale = ContentScale.None,
                colorFilter = ColorFilter.tint(colors.color),
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewObDropdownMenuItem() {
    Column {
        ObDropdownMenuItem(text = "menu_1")
        SpacerDivider()
        ObDropdownMenuItem(text = "menu_2", leadingIcon = R.drawable.chevron_right)
        SpacerDivider()
        ObDropdownMenuItem(text = "menu_2", trailingIcon = R.drawable.book)
        SpacerDivider()
        ObDropdownMenuItem(
            text = "menu_3",
            leadingIcon = R.drawable.chevron_right,
            trailingIcon = R.drawable.check_o,
            colors = ObMenuDefaults.defaultColors,
        )
        DropdownMenu(
            expanded = true,
            onDismissRequest = {},
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 0.dp,//阴影
        ) {
            ObDropdownMenuItem(text = "menu_1")
        }
    }
}

object ObMenuDefaults {

    val defaultColors: ObMenuItemColors = ObMenuItemColors(color = Black95, background = Color.White)
    val dangerColors: ObMenuItemColors = ObMenuItemColors(color = ContentRed, background = Color.White)
}

data class ObMenuItemColors(
    val color: Color,
    val background: Color,
)

internal val MenuVerticalMargin = 48.dp
private val MenuListItemContainerHeight = 48.dp
private val DropdownMenuItemHorizontalPadding = 12.dp
internal val DropdownMenuVerticalPadding = 8.dp
private val DropdownMenuItemDefaultMinWidth = 112.dp
private val DropdownMenuItemDefaultMaxWidth = 280.dp

// Menu open/close animation.
internal const val InTransitionDuration = 120
internal const val OutTransitionDuration = 75
internal const val ExpandedScaleTarget = 1f
internal const val ClosedScaleTarget = 0.8f
internal const val ExpandedAlphaTarget = 1f
internal const val ClosedAlphaTarget = 0f
