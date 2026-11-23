package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.CalcKey
import com.example.domain.KeyStyle
import com.example.ui.theme.HisaabTheme

@Composable
fun CalculatorKeypad(
    onKeyPress: (CalcKey) -> Unit,
    keyHeight: Dp = 64.dp,
    spacing: Dp = 12.dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        KeyRow(
            keys = listOf(CalcKey.AC, CalcKey.BS, CalcKey.PERCENT, CalcKey.DIVIDE),
            keyHeight = keyHeight,
            spacing = spacing,
            onKeyPress = onKeyPress,
        )
        KeyRow(
            keys = listOf(CalcKey.SEVEN, CalcKey.EIGHT, CalcKey.NINE, CalcKey.MULTIPLY),
            keyHeight = keyHeight,
            spacing = spacing,
            onKeyPress = onKeyPress,
        )
        KeyRow(
            keys = listOf(CalcKey.FOUR, CalcKey.FIVE, CalcKey.SIX, CalcKey.SUBTRACT),
            keyHeight = keyHeight,
            spacing = spacing,
            onKeyPress = onKeyPress,
        )
        KeyRow(
            keys = listOf(CalcKey.ONE, CalcKey.TWO, CalcKey.THREE, CalcKey.ADD),
            keyHeight = keyHeight,
            spacing = spacing,
            onKeyPress = onKeyPress,
        )
        // Bottom row: 0 spans exactly 2 columns + 1 inner gap, aligned to the 4-column grid above
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            CalculatorKeyButton(
                key = CalcKey.ZERO,
                keyHeight = keyHeight,
                onClick = { onKeyPress(CalcKey.ZERO) },
                modifier = Modifier.weight(1f),
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing),
            ) {
                CalculatorKeyButton(
                    key = CalcKey.DOT,
                    keyHeight = keyHeight,
                    onClick = { onKeyPress(CalcKey.DOT) },
                    modifier = Modifier.weight(1f),
                )
                CalculatorKeyButton(
                    key = CalcKey.EQUALS,
                    keyHeight = keyHeight,
                    onClick = { onKeyPress(CalcKey.EQUALS) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun KeyRow(
    keys: List<CalcKey>,
    keyHeight: Dp,
    spacing: Dp,
    onKeyPress: (CalcKey) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing),
    ) {
        keys.forEach { key ->
            CalculatorKeyButton(
                key = key,
                keyHeight = keyHeight,
                onClick = { onKeyPress(key) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CalculatorKeyButton(
    key: CalcKey,
    keyHeight: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HisaabTheme.colors
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = "key_scale",
    )

    val shape = RoundedCornerShape(18.dp)
    val contentDesc = keyContentDescription(key)

    val backgroundModifier = when (key.style) {
        KeyStyle.STANDARD, KeyStyle.ALL_CLEAR -> {
            Modifier
                .background(colors.glassKey)
                .border(1.dp, colors.glassBorder, shape)
        }
        KeyStyle.OPERATOR -> {
            Modifier.background(colors.operatorBg)
        }
        KeyStyle.EQUALS -> {
            Modifier.background(
                Brush.linearGradient(
                    colors = listOf(colors.equalsStart, colors.equalsEnd),
                ),
            )
        }
    }

    val contentColor = when (key.style) {
        KeyStyle.STANDARD -> colors.textPrimary
        KeyStyle.ALL_CLEAR -> colors.allClearAccent
        KeyStyle.OPERATOR, KeyStyle.EQUALS -> colors.operatorText
    }

    val rippleColor = when (key.style) {
        KeyStyle.OPERATOR, KeyStyle.EQUALS -> Color.Black
        KeyStyle.STANDARD, KeyStyle.ALL_CLEAR -> Color.White
    }

    Box(
        modifier = modifier
            .height(keyHeight.coerceAtLeast(52.dp))
            .minimumInteractiveComponentSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .then(backgroundModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = rippleColor),
                role = Role.Button,
                onClick = {
                    val feedback = if (key == CalcKey.EQUALS) {
                        HapticFeedbackType.LongPress
                    } else {
                        HapticFeedbackType.TextHandleMove
                    }
                    haptics.performHapticFeedback(feedback)
                    onClick()
                },
            )
            .semantics {
                if (contentDesc != null) {
                    contentDescription = contentDesc
                }
            }
            .testTag(key.testTag),
        contentAlignment = Alignment.Center,
    ) {
        if (key == CalcKey.BS) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Backspace,
                contentDescription = stringResource(R.string.cd_backspace),
                tint = contentColor,
            )
        } else {
            Text(
                text = key.symbol,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = if (key == CalcKey.AC) 20.sp else 24.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = contentColor,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun keyContentDescription(key: CalcKey): String? = when (key) {
    CalcKey.AC -> stringResource(R.string.cd_all_clear)
    CalcKey.BS -> stringResource(R.string.cd_backspace)
    CalcKey.PERCENT -> stringResource(R.string.cd_percent)
    CalcKey.DIVIDE -> stringResource(R.string.cd_divide)
    CalcKey.MULTIPLY -> stringResource(R.string.cd_multiply)
    CalcKey.SUBTRACT -> stringResource(R.string.cd_subtract)
    CalcKey.ADD -> stringResource(R.string.cd_add)
    CalcKey.EQUALS -> stringResource(R.string.cd_equals)
    CalcKey.DOT -> stringResource(R.string.cd_decimal)
    else -> null
}
