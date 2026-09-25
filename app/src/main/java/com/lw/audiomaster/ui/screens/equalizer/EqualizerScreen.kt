package com.lw.audiomaster.ui.screens.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.data.model.EqPreset
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.ui.components.AppTopBar
import com.lw.audiomaster.ui.components.EqualizerView
import com.lw.audiomaster.ui.components.GlassCard
import com.lw.audiomaster.ui.components.GradientButton
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.StrokeSoft
import com.lw.audiomaster.ui.theme.Surface1
import com.lw.audiomaster.ui.theme.Surface2
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary

@Composable
fun EqualizerScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val settings by vm.master.collectAsState()
    val isPro by vm.isPro.collectAsState()

    Box(Modifier.fillMaxSize().background(Gradients.screenBackground)) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            AppTopBar(
                title = "Equalizer",
                onBack = onBack,
                trailing = {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface2)
                            .clickable { vm.applyPreset(EqPreset.flat) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.RestartAlt, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reset", color = SkyBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            )

            Column(Modifier.padding(20.dp).weight(1f)) {
                GlassCard(Modifier.fillMaxWidth()) {
                    Text("6-Band EQ", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Drag each fader between -12 dB and +12 dB", color = TextMuted, fontSize = 12.sp)
                    Spacer(Modifier.height(18.dp))
                    EqualizerView(
                        bands = settings.bands,
                        onBandChange = { i, v -> vm.setBand(i, v) }
                    )
                }

                Spacer(Modifier.height(18.dp))
                Text("Quick presets", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EqPreset.all.forEach { p ->
                        val locked = p.isPro && !isPro
                        val selected = p.id == settings.presetId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .then(if (selected) Modifier.background(Gradients.brandHorizontal) else Modifier.background(Surface1))
                                .border(1.dp, if (selected) Color.Transparent else StrokeSoft, RoundedCornerShape(12.dp))
                                .clickable { if (!locked) vm.applyPreset(p) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                (if (locked) "\uD83D\uDD12 " else "") + p.name,
                                color = if (selected) Color.White else if (locked) TextMuted else TextPrimary,
                                fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                GradientButton(text = "Done", onClick = onDone)
            }
        }
    }
}
