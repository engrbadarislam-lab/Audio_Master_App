package com.lw.audiomaster.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.ElectricBlue
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.Surface3

/**
 * Vertical-bar waveform. [progress] (0f..1f) tints the already-played portion.
 */
@Composable
fun WaveformView(
    bars: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 120.dp,
    progress: Float = 0f,
    playedBrush: Brush = Brush.verticalGradient(listOf(CyanGlow, SkyBlue, ElectricBlue))
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (bars.isEmpty()) return@Canvas
        val n = bars.size
        val gap = 3f
        val barWidth = (size.width - gap * (n - 1)) / n
        val centerY = size.height / 2f
        val playedCount = (n * progress).toInt()

        bars.forEachIndexed { i, amp ->
            val h = (amp * size.height).coerceIn(4f, size.height)
            val x = i * (barWidth + gap)
            val top = centerY - h / 2f
            if (i <= playedCount && progress > 0f) {
                drawRoundRectBar(x, top, barWidth, h, playedBrush)
            } else {
                drawRoundRectBarSolid(x, top, barWidth, h)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRoundRectBar(
    x: Float, top: Float, w: Float, h: Float, brush: Brush
) {
    drawRoundRect(
        brush = brush,
        topLeft = Offset(x, top),
        size = Size(w, h),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(w / 2f, w / 2f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRoundRectBarSolid(
    x: Float, top: Float, w: Float, h: Float
) {
    drawRoundRect(
        color = Surface3,
        topLeft = Offset(x, top),
        size = Size(w, h),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(w / 2f, w / 2f)
    )
}

/** Two stacked mini waveforms for the A/B comparison panel. */
@Composable
fun MiniWaveform(
    bars: List<Float>,
    active: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 44.dp
) {
    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        if (bars.isEmpty()) return@Canvas
        val n = bars.size
        val gap = 2f
        val barWidth = (size.width - gap * (n - 1)) / n
        val centerY = size.height / 2f
        val brush = if (active)
            Brush.verticalGradient(listOf(CyanGlow, SkyBlue))
        else Brush.verticalGradient(listOf(Surface3, Surface3))
        bars.forEachIndexed { i, amp ->
            val h = (amp * size.height).coerceIn(3f, size.height)
            val x = i * (barWidth + gap)
            drawLine(
                brush = brush,
                start = Offset(x + barWidth / 2f, centerY - h / 2f),
                end = Offset(x + barWidth / 2f, centerY + h / 2f),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
