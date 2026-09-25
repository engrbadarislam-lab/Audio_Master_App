package com.lw.audiomaster.ui.screens.export

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.ui.components.GradientButton
import com.lw.audiomaster.ui.components.OutlineButton
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.StrokeSoft
import com.lw.audiomaster.ui.theme.SuccessGreen
import com.lw.audiomaster.ui.theme.Surface1
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary

@Composable
fun ExportSuccessScreen(
    vm: AppViewModel,
    onDone: () -> Unit,
    onOpenLibrary: () -> Unit
) {
    val source by vm.source.collectAsState()
    val format by vm.exportFormat.collectAsState()

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(110.dp).clip(CircleShape).background(Gradients.glow(0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(Gradients.brandVertical),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(46.dp))
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text("Master complete!", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Your mastered track has been saved.",
                    color = TextSecondary, fontSize = 15.sp, textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(28.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Gradients.card)
                        .border(1.dp, StrokeSoft, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(Gradients.brandVertical),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            source?.name?.substringBeforeLast('.')?.let { "$it (mastered)" } ?: "Mastered track",
                            color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, maxLines = 1
                        )
                        Text(format.label, color = TextMuted, fontSize = 12.sp)
                    }
                    Icon(Icons.Rounded.PlayCircle, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(30.dp))
                }

                Spacer(Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShareChip(Icons.Rounded.IosShare, "Share", Modifier.weight(1f))
                    ShareChip(Icons.Rounded.Folder, "Files", Modifier.weight(1f))
                    ShareChip(Icons.Rounded.MusicNote, "Library", Modifier.weight(1f), onOpenLibrary)
                }
            }

            OutlineButton(text = "View in Library", leadingIcon = Icons.Rounded.MusicNote, onClick = onOpenLibrary)
            Spacer(Modifier.height(12.dp))
            GradientButton(text = "Done", onClick = onDone)
        }
    }
}

@Composable
private fun ShareChip(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
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
        Spacer(Modifier.height(6.dp))
        Text(label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
