package cn.coolbet.orbit.ui.kit

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import cn.coolbet.orbit.ui.theme.Black08
import cn.coolbet.orbit.ui.theme.Black95
import cn.coolbet.orbit.ui.theme.OrbitTheme

@Composable
fun ObSwitch (
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 48.dp,
    height: Dp = 28.dp,
    thumbSize: Dp = 20.dp,
    animationDuration: Int = 200,
    checkedTrackColor: Color = Black95,
    uncheckedTrackColor: Color = Black08,
    thumbColor: Color = Color.White
) {
    // 1. 轨道颜色动画
    val trackColor by animateColorAsState(
        targetValue = if (checked) checkedTrackColor else uncheckedTrackColor,
        animationSpec = tween(animationDuration),
        label = "TrackColor"
    )

    // 2. 核心移动逻辑：动画化 Alignment 的 X 轴偏置值 (-1f 到 1f)
    val alignmentBias by animateFloatAsState(
        targetValue = if (checked) 1f else -1f, // -1f 对应 CenterStart, 1f 对应 CenterEnd
        animationSpec = tween(durationMillis = animationDuration),
        label = "AlignmentBias"
    )

    // 3. 将动画化的偏置值转换为 Alignment 对象
    val thumbAlignment = remember(alignmentBias) {
        BiasAlignment(horizontalBias = alignmentBias, verticalBias = 0f)
    }

    val haptics = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(99.dp))
            .background(trackColor)
            .padding(4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                val feedbackType = if (checked) {
                    HapticFeedbackType.ToggleOff
                } else {
                    HapticFeedbackType.ToggleOn
                }
                onCheckedChange(!checked)
                haptics.performHapticFeedback(feedbackType)
            },
        contentAlignment = thumbAlignment
    ) {
        // 滑块 (Thumb)
        Spacer(
            modifier = Modifier
                .size(thumbSize)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun SwitchComposeDemo() {
    OrbitTheme {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            var defaultChecked by remember { mutableStateOf(false) }
            Text("Default Custom Size (48x28)", style = MaterialTheme.typography.bodyLarge)
            ObSwitch(
                checked = defaultChecked,
                onCheckedChange = { defaultChecked = it }
            )
            Switch(checked = true, onCheckedChange = {})

            var largeChecked by remember { mutableStateOf(true) }
            Text("Large Switch (64x36)", style = MaterialTheme.typography.bodyLarge)
            ObSwitch(
                checked = largeChecked,
                onCheckedChange = { largeChecked = it },
                width = 64.dp,
                height = 36.dp,
                thumbSize = 28.dp,
            )
        }
    }
}