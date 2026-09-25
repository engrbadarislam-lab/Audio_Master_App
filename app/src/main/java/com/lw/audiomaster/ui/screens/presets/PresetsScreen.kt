package com.lw.audiomaster.ui.screens.presets

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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
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
import com.lw.audiomaster.ui.components.GradientButton
import com.lw.audiomaster.ui.components.ProBadge
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
fun PresetsScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onUpgrade: () -> Unit,
    onApplied: () -> Unit
) {
    val isPro by vm.isPro.collectAsState()
    val settings by vm.master.collectAsState()

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppTopBar(title = "Presets", onBack = onBack)
            Text(
                "Tap a preset to shape your sound. Fine-tune later in the equalizer.",
                color = TextSecondary, fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
            ) {
                items(EqPreset.all, key = { it.id }) { preset ->
                    val locked = preset.isPro && !isPro
                    val selected = preset.id == settings.presetId
                    PresetCard(
                        preset = preset,
                        selected = selected,
                        locked = locked,
                        onClick = {
                            if (locked) onUpgrade()
                            else { vm.applyPreset(preset); onApplied() }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetCard(
    preset: EqPreset,
    selected: Boolean,
    locked: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(18.dp))
            .then(
                if (selected) Modifier.background(Gradients.brandVertical)
                else Modifier.background(Gradients.card)
            )
            .border(1.dp, if (selected) Color.Transparent else StrokeSoft, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Text(preset.emoji, fontSize = 30.sp, modifier = Modifier.weight(1f))
            if (locked) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Surface1).padding(4.dp)
                ) {
                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = SkyBlue, modifier = Modifier.height(14.dp))
                }
            } else if (preset.isPro) {
                ProBadge()
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            preset.name,
            color = if (selected) Color.White else TextPrimary,
            fontWeight = FontWeight.Bold, fontSize = 16.sp
        )
        Text(
            if (selected) "Active" else "Tap to apply",
            color = if (selected) Color.White.copy(alpha = 0.8f) else TextMuted,
            fontSize = 11.sp
        )
    }
}
