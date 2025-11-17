package cn.coolbet.orbit.ui.kit

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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Stable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import cn.coolbet.orbit.R
import cn.coolbet.orbit.ui.theme.AppTypography
import cn.coolbet.orbit.ui.theme.Black95
import cn.coolbet.orbit.ui.theme.ContentRed


// =================================================================
// II. 你提供的核心定位逻辑 (CustomDropdownPositionProvider)
// =================================================================

internal val MenuVerticalMargin = 48.dp
private val MenuListItemContainerHeight = 48.dp
private val DropdownMenuItemHorizontalPadding = 12.dp
internal val DropdownMenuVerticalPadding = 8.dp
private val DropdownMenuItemDefaultMinWidth = 112.dp
private val DropdownMenuItemDefaultMaxWidth = 280.dp

@Stable
internal object MenuPosition {
    /**
     * An interface to calculate the vertical position of a menu with respect to its anchor and
     * window. The returned y-coordinate is relative to the window.
     *
     * @see PopupPositionProvider
     */
    @Stable
    fun interface Vertical {
        fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuHeight: Int,
        ): Int
    }

    /**
     * An interface to calculate the horizontal position of a menu with respect to its anchor,
     * window, and layout direction. The returned x-coordinate is relative to the window.
     *
     * @see PopupPositionProvider
     */
    @Stable
    fun interface Horizontal {
        fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuWidth: Int,
            layoutDirection: LayoutDirection,
        ): Int
    }

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the start of the menu to the start of the
     * anchor.
     *
     * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
     * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
     */
    fun startToAnchorStart(offset: Int = 0): Horizontal =
        AnchorAlignmentOffsetPosition.Horizontal(
            menuAlignment = Alignment.Start,
            anchorAlignment = Alignment.Start,
            offset = offset,
        )

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the end of the menu to the end of the
     * anchor.
     *
     * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
     * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
     */
    fun endToAnchorEnd(offset: Int = 0): Horizontal =
        AnchorAlignmentOffsetPosition.Horizontal(
            menuAlignment = Alignment.End,
            anchorAlignment = Alignment.End,
            offset = offset,
        )

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the left of the menu to the left of the
     * window.
     *
     * The resulting x position will be coerced so that the menu remains within the area inside the
     * given [margin] from the left and right edges of the window.
     */
    fun leftToWindowLeft(margin: Int = 0): Horizontal =
        WindowAlignmentMarginPosition.Horizontal(
            alignment = AbsoluteAlignment.Left,
            margin = margin,
        )

    /**
     * Returns a [MenuPosition.Horizontal] which aligns the right of the menu to the right of the
     * window.
     *
     * The resulting x position will be coerced so that the menu remains within the area inside the
     * given [margin] from the left and right edges of the window.
     */
    fun rightToWindowRight(margin: Int = 0): Horizontal =
        WindowAlignmentMarginPosition.Horizontal(
            alignment = AbsoluteAlignment.Right,
            margin = margin,
        )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the top of the menu to the bottom of the
     * anchor.
     */
    fun topToAnchorBottom(offset: Int = 0): Vertical =
        AnchorAlignmentOffsetPosition.Vertical(
            menuAlignment = Alignment.Top,
            anchorAlignment = Alignment.Bottom,
            offset = offset,
        )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the bottom of the menu to the top of the
     * anchor.
     */
    fun bottomToAnchorTop(offset: Int = 0): Vertical =
        AnchorAlignmentOffsetPosition.Vertical(
            menuAlignment = Alignment.Bottom,
            anchorAlignment = Alignment.Top,
            offset = offset,
        )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the center of the menu to the top of the
     * anchor.
     */
    fun centerToAnchorTop(offset: Int = 0): Vertical =
        AnchorAlignmentOffsetPosition.Vertical(
            menuAlignment = Alignment.CenterVertically,
            anchorAlignment = Alignment.Top,
            offset = offset,
        )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the top of the menu to the top of the window.
     *
     * The resulting y position will be coerced so that the menu remains within the area inside the
     * given [margin] from the top and bottom edges of the window.
     */
    fun topToWindowTop(margin: Int = 0): Vertical =
        WindowAlignmentMarginPosition.Vertical(
            alignment = Alignment.Top,
            margin = margin,
        )

    /**
     * Returns a [MenuPosition.Vertical] which aligns the bottom of the menu to the bottom of the
     * window.
     *
     * The resulting y position will be coerced so that the menu remains within the area inside the
     * given [margin] from the top and bottom edges of the window.
     */
    fun bottomToWindowBottom(margin: Int = 0): Vertical =
        WindowAlignmentMarginPosition.Vertical(
            alignment = Alignment.Bottom,
            margin = margin,
        )
}

@Immutable
internal object AnchorAlignmentOffsetPosition {
    /**
     * A [MenuPosition.Horizontal] which horizontally aligns the given [menuAlignment] with the
     * given [anchorAlignment].
     *
     * The given [offset] is [LayoutDirection]-aware. It will be added to the resulting x position
     * for [LayoutDirection.Ltr] and subtracted for [LayoutDirection.Rtl].
     */
    @Immutable
    data class Horizontal(
        private val menuAlignment: Alignment.Horizontal,
        private val anchorAlignment: Alignment.Horizontal,
        private val offset: Int,
    ) : MenuPosition.Horizontal {
        override fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuWidth: Int,
            layoutDirection: LayoutDirection,
        ): Int {
            val anchorAlignmentOffset =
                anchorAlignment.align(
                    size = 0,
                    space = anchorBounds.width,
                    layoutDirection = layoutDirection,
                )
            val menuAlignmentOffset =
                -menuAlignment.align(
                    size = 0,
                    space = menuWidth,
                    layoutDirection,
                )
            val resolvedOffset = if (layoutDirection == LayoutDirection.Ltr) offset else -offset
            return anchorBounds.left + anchorAlignmentOffset + menuAlignmentOffset + resolvedOffset
        }
    }

    /**
     * A [MenuPosition.Vertical] which vertically aligns the given [menuAlignment] with the given
     * [anchorAlignment].
     */
    @Immutable
    data class Vertical(
        private val menuAlignment: Alignment.Vertical,
        private val anchorAlignment: Alignment.Vertical,
        private val offset: Int,
    ) : MenuPosition.Vertical {
        override fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuHeight: Int,
        ): Int {
            val anchorAlignmentOffset =
                anchorAlignment.align(
                    size = 0,
                    space = anchorBounds.height,
                )
            val menuAlignmentOffset =
                -menuAlignment.align(
                    size = 0,
                    space = menuHeight,
                )
            return anchorBounds.top + anchorAlignmentOffset + menuAlignmentOffset + offset
        }
    }
}

@Immutable
internal object WindowAlignmentMarginPosition {
    /**
     * A [MenuPosition.Horizontal] which horizontally aligns the menu within the window according to
     * the given [alignment].
     *
     * The resulting x position will be coerced so that the menu remains within the area inside the
     * given [margin] from the left and right edges of the window. If this is not possible, i.e.,
     * the menu is too wide, then it is centered horizontally instead.
     */
    @Immutable
    data class Horizontal(
        private val alignment: Alignment.Horizontal,
        private val margin: Int,
    ) : MenuPosition.Horizontal {
        override fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuWidth: Int,
            layoutDirection: LayoutDirection,
        ): Int {
            if (menuWidth >= windowSize.width - 2 * margin) {
                return Alignment.CenterHorizontally.align(
                    size = menuWidth,
                    space = windowSize.width,
                    layoutDirection = layoutDirection,
                )
            }
            val x =
                alignment.align(
                    size = menuWidth,
                    space = windowSize.width,
                    layoutDirection = layoutDirection,
                )
            return x.coerceIn(margin, windowSize.width - margin - menuWidth)
        }
    }

    /**
     * A [MenuPosition.Vertical] which vertically aligns the menu within the window according to the
     * given [alignment].
     *
     * The resulting y position will be coerced so that the menu remains within the area inside the
     * given [margin] from the top and bottom edges of the window. If this is not possible, i.e.,
     * the menu is too tall, then it is centered vertically instead.
     */
    @Immutable
    data class Vertical(
        private val alignment: Alignment.Vertical,
        private val margin: Int,
    ) : MenuPosition.Vertical {
        override fun position(
            anchorBounds: IntRect,
            windowSize: IntSize,
            menuHeight: Int,
        ): Int {
            if (menuHeight >= windowSize.height - 2 * margin) {
                return Alignment.CenterVertically.align(
                    size = menuHeight,
                    space = windowSize.height,
                )
            }
            val y =
                alignment.align(
                    size = menuHeight,
                    space = windowSize.height,
                )
            return y.coerceIn(margin, windowSize.height - margin - menuHeight)
        }
    }
}


@Immutable
internal data class CustomDropdownPositionProvider(
    val contentOffset: DpOffset,
    val density: Density,
    val verticalMargin: Int = with(density) { MenuVerticalMargin.roundToPx() },
    val onPositionCalculated: (anchorBounds: IntRect, menuBounds: IntRect) -> Unit = { _, _ -> }
) : PopupPositionProvider {
    // Horizontal position
    private val startToAnchorStart: MenuPosition.Horizontal
    private val endToAnchorEnd: MenuPosition.Horizontal
    private val leftToWindowLeft: MenuPosition.Horizontal
    private val rightToWindowRight: MenuPosition.Horizontal
    // Vertical position
    private val topToAnchorBottom: MenuPosition.Vertical
    private val bottomToAnchorTop: MenuPosition.Vertical
    private val centerToAnchorTop: MenuPosition.Vertical
    private val topToWindowTop: MenuPosition.Vertical
    private val bottomToWindowBottom: MenuPosition.Vertical

    init {
        // Horizontal position
        val contentOffsetX = with(density) { contentOffset.x.roundToPx() }
        startToAnchorStart = MenuPosition.startToAnchorStart(offset = contentOffsetX)
        endToAnchorEnd = MenuPosition.endToAnchorEnd(offset = contentOffsetX)
        leftToWindowLeft = MenuPosition.leftToWindowLeft(margin = 0)
        rightToWindowRight = MenuPosition.rightToWindowRight(margin = 0)
        // Vertical position
        val contentOffsetY = with(density) { contentOffset.y.roundToPx() }
        topToAnchorBottom = MenuPosition.topToAnchorBottom(offset = contentOffsetY)
        bottomToAnchorTop = MenuPosition.bottomToAnchorTop(offset = contentOffsetY)
        centerToAnchorTop = MenuPosition.centerToAnchorTop(offset = contentOffsetY)
        topToWindowTop = MenuPosition.topToWindowTop(margin = verticalMargin)
        bottomToWindowBottom = MenuPosition.bottomToWindowBottom(margin = verticalMargin)
    }

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val xCandidates =
            listOf(
                startToAnchorStart,
                endToAnchorEnd,
                if (anchorBounds.center.x < windowSize.width / 2) {
                    leftToWindowLeft
                } else {
                    rightToWindowRight
                }
            )
        var x = 0
        for (index in xCandidates.indices) {
            val xCandidate =
                xCandidates[index].position(
                    anchorBounds = anchorBounds,
                    windowSize = windowSize,
                    menuWidth = popupContentSize.width,
                    layoutDirection = layoutDirection
                )
            if (
                index == xCandidates.lastIndex ||
                (xCandidate >= 0 && xCandidate + popupContentSize.width <= windowSize.width)
            ) {
                x = xCandidate
                break
            }
        }

        val yCandidates =
            listOf(
                topToAnchorBottom,
                bottomToAnchorTop,
                centerToAnchorTop,
                if (anchorBounds.center.y < windowSize.height / 2) {
                    topToWindowTop
                } else {
                    bottomToWindowBottom
                }
            )
        var y = 0
        for (index in yCandidates.indices) {
            val yCandidate =
                yCandidates[index].position(
                    anchorBounds = anchorBounds,
                    windowSize = windowSize,
                    menuHeight = popupContentSize.height
                )
            if (
                index == yCandidates.lastIndex ||
                (yCandidate >= verticalMargin &&
                        yCandidate + popupContentSize.height <= windowSize.height - verticalMargin)
            ) {
                y = yCandidate
                break
            }
        }

        val menuOffset = IntOffset(x, y)
        onPositionCalculated(
            /* anchorBounds = */ anchorBounds,
            /* menuBounds = */ IntRect(offset = menuOffset, size = popupContentSize)
        )
        return menuOffset
    }
}


@Composable
fun CustomDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    if (expanded) {
        val density = LocalDensity.current

        // 1. 创建你的自定义定位提供者
        val positionProvider = remember(density) {
            CustomDropdownPositionProvider(
                contentOffset = DpOffset(0.dp, 0.dp), // 默认不偏移
                density = density
            )
        }

        // 2. 使用 Compose 的 Popup 组件
        Popup(
            onDismissRequest = onDismissRequest,
            // 3. 将你的定位提供者传递给 Popup
            popupPositionProvider = positionProvider,
            // Clip 设置为 false 允许菜单内容绘制到窗口边界之外（如果需要阴影或边框）
            // clip = false
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                content()
            }
        }
    }
}

@Composable
fun ObDropdownMenuItem(
    text: String = "",
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    colors: ObMenuItemColors = ObMenuDefaults.defaultColors,
){
    Row(
        modifier = Modifier.height(44.dp).width(240.dp)
            .padding(all = 0.dp)
            .background(colors.background),
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
            modifier = Modifier.weight(1f)
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

    val defaultColors: ObMenuItemColors = ObMenuItemColors(color = Black95, background = Color.Red)
    val dangerColors: ObMenuItemColors = ObMenuItemColors(color = ContentRed, background = Color.White)
}

data class ObMenuItemColors(
    val color: Color,
    val background: Color,
)