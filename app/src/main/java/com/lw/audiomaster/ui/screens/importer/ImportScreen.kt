package com.lw.audiomaster.ui.screens.importer

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.ui.components.AppTopBar
import com.lw.audiomaster.ui.components.ScreenBackground
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
fun ImportScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onLoaded: () -> Unit
) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppTopBar(title = "New Master", onBack = onBack)

            Column(Modifier.padding(20.dp)) {
                Text(
                    "Choose a source to master",
                    color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Import any audio or video file. Your original is never overwritten.",
                    color = TextSecondary, fontSize = 14.sp, lineHeight = 20.sp
                )
                Spacer(Modifier.height(24.dp))

                SourceOption(
                    icon = Icons.Rounded.MusicNote,
                    title = "Audio file",
                    subtitle = "WAV, MP3, M4A, FLAC, AAC…",
                    onClick = { vm.loadDemoSource("My Song.wav"); onLoaded() }
                )
                Spacer(Modifier.height(12.dp))
                SourceOption(
                    icon = Icons.Rounded.Videocam,
                    title = "Video file",
                    subtitle = "Extract & master the audio (MP4, MOV…)",
                    onClick = { vm.loadDemoSource("Vlog Take 3.mp4", isVideo = true); onLoaded() }
                )
                Spacer(Modifier.height(12.dp))
                SourceOption(
                    icon = Icons.Rounded.Mic,
                    title = "Record now",
                    subtitle = "Capture a voice note or take",
                    onClick = { vm.loadDemoSource("New Recording.m4a"); onLoaded() }
                )
                Spacer(Modifier.height(12.dp))
                SourceOption(
                    icon = Icons.Rounded.AudioFile,
                    title = "Sample track",
                    subtitle = "Try the app with a demo file",
                    onClick = { vm.loadDemoSource("Demo Beat.wav"); onLoaded() }
                )

                Spacer(Modifier.height(28.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Surface1)
                        .border(1.dp, StrokeSoft, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.AudioFile, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Supported: WAV · MP3 · M4A · AAC · FLAC · MP4 · MOV",
                            color = TextMuted, fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceOption(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Gradients.card)
            .border(1.dp, StrokeSoft, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(Gradients.brandVertical),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = TextMuted, fontSize = 12.sp)
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextMuted)
    }
}
