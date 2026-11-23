package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.ReactionBanner
import com.example.domain.ReactionKind
import com.example.ui.theme.HisaabTheme
import kotlinx.coroutines.launch

@Composable
fun ReactionBannerCard(
    reaction: ReactionBanner?,
    modifier: Modifier = Modifier,
) {
    val colors = HisaabTheme.colors
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .semantics { liveRegion = LiveRegionMode.Assertive },
        contentAlignment = Alignment.Center,
    ) {
        if (reaction == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .clip(shape)
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, colors.glassBorder.copy(alpha = 0.08f), shape)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("reaction_banner_idle"),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.label_banner_idle_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            val scale = remember(reaction.id) { Animatable(0.84f) }
            val offsetY = remember(reaction.id) { Animatable(-18f) }
            val alpha = remember(reaction.id) { Animatable(0f) }

            LaunchedEffect(reaction.id) {
                launch {
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                    )
                }
                launch {
                    offsetY.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                    )
                }
                launch {
                    alpha.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium,
                        ),
                    )
                }
            }

            val (brush, borderColor, textColor) = when (reaction.kind) {
                ReactionKind.NEGATIVE -> Triple(
                    Brush.linearGradient(listOf(colors.negStart, colors.negEnd)),
                    colors.negBorder,
                    colors.negText,
                )
                ReactionKind.POSITIVE -> Triple(
                    Brush.linearGradient(listOf(colors.posStart, colors.posEnd)),
                    colors.posBorder,
                    colors.posText,
                )
                ReactionKind.ZERO -> Triple(
                    Brush.linearGradient(listOf(colors.zeroStart, colors.zeroEnd)),
                    colors.zeroBorder,
                    colors.zeroText,
                )
                ReactionKind.ERROR -> Triple(
                    Brush.linearGradient(listOf(colors.errBg, colors.errBg)),
                    colors.errBorder,
                    colors.errText,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        translationY = offsetY.value
                        this.alpha = alpha.value
                    }
                    .clip(shape)
                    .background(brush)
                    .border(1.dp, borderColor, shape)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("reaction_banner"),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(reaction.messageResId),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                    ),
                    color = textColor,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
