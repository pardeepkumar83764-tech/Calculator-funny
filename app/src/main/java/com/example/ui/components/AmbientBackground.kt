package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.HisaabTheme

@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = HisaabTheme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvasBackground),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minDim = size.minDimension
            val indigoRadius = minDim * 0.85f
            val amberRadius = minDim * 0.78f

            // Top-left luminous indigo ambient glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.glowPrimary.copy(alpha = 0.42f),
                        colors.glowPrimary.copy(alpha = 0.14f),
                        Color.Transparent,
                    ),
                    center = Offset(x = size.width * 0.08f, y = size.height * 0.06f),
                    radius = indigoRadius,
                ),
                radius = indigoRadius,
                center = Offset(x = size.width * 0.08f, y = size.height * 0.06f),
            )

            // Bottom-right warm amber ambient glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.glowSecondary.copy(alpha = 0.30f),
                        colors.glowSecondary.copy(alpha = 0.10f),
                        Color.Transparent,
                    ),
                    center = Offset(x = size.width * 0.94f, y = size.height * 0.92f),
                    radius = amberRadius,
                ),
                radius = amberRadius,
                center = Offset(x = size.width * 0.94f, y = size.height * 0.92f),
            )
        }
        content()
    }
}
