package com.lw.audiomaster.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.data.model.EqPreset
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.ui.components.AppTopBar
import com.lw.audiomaster.ui.components.GlassCard
import com.lw.audiomaster.ui.components.GradientButton
import com.lw.audiomaster.ui.components.MiniWaveform
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.components.StatPill
import com.lw.audiomaster.ui.components.WaveformView
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.StrokeSoft
import com.lw.audiomaster.ui.theme.Surface1
import com.lw.audiomaster.ui.theme.Surface2
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary

@Composable
fun EditorScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onOpenPresets: () -> Unit,
    onOpenEq: () -> Unit,
    onOpenVolume: () -> Unit,
    onExport: () -> Unit
) {
    val source by vm.source.collectAsState()
    val waveform by vm.waveform.collectAsState()
    val mastered by vm.mastered.collectAsState()
    val settings by vm.master.collectAsState()

    var playing by remember { mutableStateOf(false) }
    var showMastered by remember { mutableStateOf(true) }

    val presetName = EqPreset.all.firstOrNull { it.id == settings.presetId }?.name ?: "Custom"
    val lufs = vm.estimatedLufs()

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppTopBar(title = source?.name ?: "Editor", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                // Big waveform card
                GlassCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (showMastered) "Mastered" else "Original",
                            color = if (showMastered) CyanGlow else TextSecondary,
                            fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f)
                        )
                        Text(source?.let { formatDuration(it.durationMs) } ?: "0:00", color = TextMuted, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(14.dp))
                    WaveformView(
                        bars = if (showMastered && mastered.isNotEmpty()) mastered else waveform,
                        progress = 0.42f
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Gradients.brandVertical)
                                .clickable { playing = !playing },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(
                            if (playing) "Playing preview…" else "Tap to preview",
                            color = TextSecondary, fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // A/B compare
                GlassCard(Modifier.fillMaxWidth()) {
                    Text("A / B Compare", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(Modifier.height(12.dp))
                    AbRow("A · Original", waveform, active = !showMastered) { showMastered = false }
                    Spacer(Modifier.height(10.dp))
                    AbRow("B · Mastered", mastered, active = showMastered) { showMastered = true }
                }

                Spacer(Modifier.height(16.dp))

                // Loudness stats
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatPill("Loudness", "${lufs.toInt()} LUFS", modifier = Modifier.weight(1f))
                    StatPill("Ceiling", "${settings.limiterCeilingDb} dB", accent = SkyBlue, modifier = Modifier.weight(1f))
                    StatPill("Preset", presetName, accent = CyanGlow, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(16.dp))

                // Tools
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ToolChip(Icons.Rounded.Tune, "Presets", Modifier.weight(1f), onOpenPresets)
                    ToolChip(Icons.Rounded.Equalizer, "EQ", Modifier.weight(1f), onOpenEq)
                    ToolChip(Icons.Rounded.VolumeUp, "Volume", Modifier.weight(1f), onOpenVolume)
                }

                Spacer(Modifier.height(24.dp))
            }

            Column(Modifier.padding(20.dp)) {
                GradientButton(
                    text = "Export mastered audio",
                    leadingIcon = Icons.Rounded.AutoAwesome,
                    onClick = onExport
                )
            }
        }
    }
}

@Composable
private fun AbRow(label: String, bars: List<Float>, active: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) Surface2 else Surface1)
            .border(1.dp, if (active) SkyBlue.copy(alpha = 0.6f) else StrokeSoft, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = if (active) TextPrimary else TextMuted, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.width(96.dp))
        Spacer(Modifier.width(8.dp))
        MiniWaveform(bars = bars, active = active, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ToolChip(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Surface1)
            .border(1.dp, StrokeSoft, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(8.dp))
        Text(label, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

private fun formatDuration(ms: Long): String {
    val totalSec = ms / 1000
    val m = totalSec / 60
    val s = totalSec % 60
    return "%d:%02d".format(m, s)
}
