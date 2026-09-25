package com.lw.audiomaster.ui.components

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.lw.audiomaster.data.model.EqPreset
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.ElectricBlue
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.Surface3
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextSecondary

/**
 * 6 draggable vertical EQ faders. gain range -12..+12 dB.
 */
@Composable
fun EqualizerView(
    bands: List<Float>,
    onBandChange: (index: Int, value: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val range = 12f
    Row(
        modifier = modifier.fillMaxWidth().height(240.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        bands.forEachIndexed { index, gain ->
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatGain(gain),
                    color = if (gain != 0f) SkyBlue else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .width(40.dp)
                        .pointerInput(index) {
                            detectVerticalDragGestures { change, _ ->
                                change.consume()
                                val h = size.height.toFloat()
                                val norm = 1f - (change.position.y / h).coerceIn(0f, 1f)
                                val value = ((norm * 2f - 1f) * range)
                                onBandChange(index, value.coerceIn(-range, range))
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    FaderTrack(gain / range)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = EqPreset.BAND_LABELS.getOrElse(index) { "" },
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun FaderTrack(normalized: Float) {
    Canvas(modifier = Modifier.fillMaxHeight().width(40.dp)) {
        val cx = size.width / 2f
        val trackTop = 8f
        val trackBottom = size.height - 8f
        val trackH = trackBottom - trackTop
        val centerY = trackTop + trackH / 2f

        // Background rail
        drawLine(
            color = Surface3,
            start = Offset(cx, trackTop),
            end = Offset(cx, trackBottom),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        // Center reference tick
        drawLine(
            color = TextMuted,
            start = Offset(cx - 10f, centerY),
            end = Offset(cx + 10f, centerY),
            strokeWidth = 2f
        )

        val knobY = (centerY - normalized * (trackH / 2f)).coerceIn(trackTop, trackBottom)

        // Active fill from center to knob
        drawLine(
            brush = Brush.verticalGradient(listOf(CyanGlow, SkyBlue, ElectricBlue)),
            start = Offset(cx, centerY),
            end = Offset(cx, knobY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        // Knob
        drawCircle(color = androidx.compose.ui.graphics.Color.White, radius = 11f, center = Offset(cx, knobY))
        drawCircle(brush = Brush.radialGradient(listOf(SkyBlue, ElectricBlue)), radius = 8f, center = Offset(cx, knobY))
    }
}

private fun formatGain(g: Float): String {
    val v = (g * 10).toInt() / 10f
    return if (v > 0) "+$v" else "$v"
}
