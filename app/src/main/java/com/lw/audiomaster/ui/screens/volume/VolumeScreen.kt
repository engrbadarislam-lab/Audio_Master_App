package com.lw.audiomaster.ui.screens.volume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.data.model.MasterIntensity
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.ui.components.AppTopBar
import com.lw.audiomaster.ui.components.GlassCard
import com.lw.audiomaster.ui.components.GradientButton
import com.lw.audiomaster.ui.components.LabeledSlider
import com.lw.audiomaster.ui.components.ProBadge
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.components.SegmentedControl
import com.lw.audiomaster.ui.components.StatPill
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary

@Composable
fun VolumeScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onUpgrade: () -> Unit,
    onDone: () -> Unit
) {
    val settings by vm.master.collectAsState()
    val isPro by vm.isPro.collectAsState()

    val intensities = MasterIntensity.entries
    val selectedIntensity = intensities.indexOf(settings.intensity)
    val lufs = vm.estimatedLufs()

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppTopBar(title = "Volume & Limiter", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatPill("Output loudness", "${lufs.toInt()} LUFS", modifier = Modifier.weight(1f))
                    StatPill("Peak ceiling", "${settings.limiterCeilingDb} dB", accent = SkyBlue, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(18.dp))

                GlassCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = CyanGlow)
                        Spacer(Modifier.height(0.dp))
                        Text(
                            "  Loudness target",
                            color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    SegmentedControl(
                        options = intensities.map { it.label },
                        selectedIndex = selectedIntensity.coerceAtLeast(0),
                        onSelect = { idx ->
                            val target = intensities[idx]
                            if (target == MasterIntensity.MAXIMUM && !isPro) onUpgrade()
                            else vm.setIntensity(target)
                        }
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        if (!isPro) ProBadge(text = "MAX = PRO")
                    }
                }

                Spacer(Modifier.height(16.dp))

                GlassCard(Modifier.fillMaxWidth()) {
                    Text("Make-up gain", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    LabeledSlider(
                        label = "Boost amount",
                        value = settings.makeupGainDb,
                        valueRange = 0f..12f,
                        valueText = "+${settings.makeupGainDb.toInt()} dB",
                        onValueChange = { vm.setMakeupGain(it) }
                    )
                    Spacer(Modifier.height(8.dp))
                    LabeledSlider(
                        label = "Limiter ceiling",
                        value = settings.limiterCeilingDb,
                        valueRange = -3f..0f,
                        valueText = "${(settings.limiterCeilingDb * 10).toInt() / 10f} dB",
                        onValueChange = { vm.setCeiling(it) }
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "The limiter prevents clipping — audio is boosted safely without distortion.",
                        color = TextMuted, fontSize = 12.sp, lineHeight = 16.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                GlassCard(Modifier.fillMaxWidth()) {
                    Row {
                        Text("Stereo & warmth", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        if (!isPro) ProBadge()
                    }
                    Spacer(Modifier.height(8.dp))
                    LabeledSlider(
                        label = "Stereo width",
                        value = settings.stereoWidth,
                        valueRange = 0f..1f,
                        valueText = "${(settings.stereoWidth * 100).toInt()}%",
                        onValueChange = { if (isPro) vm.setStereoWidth(it) else onUpgrade() }
                    )
                    Spacer(Modifier.height(8.dp))
                    LabeledSlider(
                        label = "Analog warmth",
                        value = settings.warmth,
                        valueRange = 0f..1f,
                        valueText = "${(settings.warmth * 100).toInt()}%",
                        onValueChange = { if (isPro) vm.setWarmth(it) else onUpgrade() }
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
            Column(Modifier.padding(20.dp)) {
                GradientButton(text = "Done", onClick = onDone)
            }
        }
    }
}
