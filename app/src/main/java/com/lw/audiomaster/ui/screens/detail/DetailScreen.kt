package com.lw.audiomaster.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.data.local.ProjectEntity
import com.lw.audiomaster.di.ServiceLocator
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.ui.components.AppTopBar
import com.lw.audiomaster.ui.components.CircleIconButton
import com.lw.audiomaster.ui.components.GlassCard
import com.lw.audiomaster.ui.components.OutlineButton
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.components.StatPill
import com.lw.audiomaster.ui.components.WaveformView
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetailScreen(
    vm: AppViewModel,
    projectId: Long,
    onBack: () -> Unit
) {
    var project by remember { mutableStateOf<ProjectEntity?>(null) }
    LaunchedEffect(projectId) {
        project = ServiceLocator.container.projectRepository.get(projectId)
    }

    val engine = ServiceLocator.container.audioEngine
    val wave = remember(projectId) { engine.waveform(projectId) }

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppTopBar(
                title = "Project",
                onBack = onBack,
                trailing = {
                    CircleIconButton(Icons.Rounded.Delete, tint = TextSecondary, onClick = {
                        project?.let { vm.deleteProject(it); onBack() }
                    })
                }
            )

            val p = project
            if (p == null) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Loading…", color = TextMuted)
                }
            } else {
                Column(Modifier.padding(20.dp).weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(Gradients.brandVertical),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(p.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 19.sp, maxLines = 1)
                            Text(p.sourceName, color = TextMuted, fontSize = 12.sp, maxLines = 1)
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    GlassCard(Modifier.fillMaxWidth()) {
                        Text("Mastered waveform", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        WaveformView(bars = wave, progress = 0f)
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatPill("Preset", p.presetName, modifier = Modifier.weight(1f))
                        StatPill("Boost", "+${p.makeupGainDb.toInt()} dB", accent = SkyBlue, modifier = Modifier.weight(1f))
                        StatPill("Length", formatDuration(p.durationMs), accent = CyanGlow, modifier = Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(16.dp))
                    GlassCard(Modifier.fillMaxWidth()) {
                        DetailRow("Format", p.format)
                        DetailRow("Status", if (p.exported) "Exported" else "Draft")
                        DetailRow("Created", formatDate(p.createdAt))
                        if (p.outputPath != null) DetailRow("Location", p.outputPath)
                    }

                    Spacer(Modifier.weight(1f))
                    OutlineButton(text = "Delete project", leadingIcon = Icons.Rounded.Delete, onClick = {
                        vm.deleteProject(p); onBack()
                    })
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextMuted, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatDuration(ms: Long): String {
    val totalSec = ms / 1000
    return "%d:%02d".format(totalSec / 60, totalSec % 60)
}

private fun formatDate(ts: Long): String =
    SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault()).format(Date(ts))
