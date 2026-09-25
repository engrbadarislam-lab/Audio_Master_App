package com.lw.audiomaster.ui.screens.export

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.ads.AdLoadingDialog
import com.lw.audiomaster.ads.AdManager
import com.lw.audiomaster.data.model.ExportFormat
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.ui.util.findActivity
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
fun ExportScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onUpgrade: () -> Unit,
    onExported: () -> Unit
) {
    val isPro by vm.isPro.collectAsState()
    val selectedFormat by vm.exportFormat.collectAsState()
    val context = LocalContext.current

    var processing by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(progress, label = "progress")

    /** True while the export interstitial is loading → shows AdLoadingDialog. */
    var adLoading by remember { mutableStateOf(false) }

    LaunchedEffect(processing) {
        if (processing) {
            vm.processFlow().collect { p -> progress = p }
            vm.saveCurrentProject(exported = true, outputPath = "/AudioMaster/exports")

            val activity = context.findActivity()
            if (activity != null && AdManager.canShowAds) {
                // Premium users are skipped inside AdManager.
                AdManager.showInterstitial(
                    activity = activity,
                    screenKey = "ads_export_inter",
                    onLoading = { adLoading = it },
                    onDone = {
                        adLoading = false
                        onExported()
                    }
                )
            } else {
                onExported()
            }
        }
    }

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppTopBar(title = "Export", onBack = onBack)

            if (processing) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(120.dp),
                            color = SkyBlue,
                            trackColor = Surface2,
                            strokeWidth = 8.dp
                        )
                        Text("${(animatedProgress * 100).toInt()}%", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("Mastering your audio…", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Applying EQ, gain and limiter", color = TextSecondary, fontSize = 13.sp)
                }
            } else {
                Column(Modifier.padding(20.dp).weight(1f)) {
                    Text("Choose export format", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Your original file is kept untouched.", color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(18.dp))

                    ExportFormat.entries.forEach { fmt ->
                        val locked = fmt.isPro && !isPro
                        FormatRow(
                            format = fmt,
                            selected = fmt == selectedFormat,
                            locked = locked,
                            onClick = { if (locked) onUpgrade() else vm.setExportFormat(fmt) }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
                Column(Modifier.padding(20.dp)) {
                    GradientButton(
                        text = "Master & Export",
                        onClick = { progress = 0f; processing = true }
                    )
                }
            }
        }
    }

    if (adLoading) AdLoadingDialog()
}

@Composable
private fun FormatRow(
    format: ExportFormat,
    selected: Boolean,
    locked: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Surface2 else Surface1)
            .border(if (selected) 2.dp else 1.dp, if (selected) SkyBlue else StrokeSoft, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(format.label, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(".${format.ext} file", color = TextMuted, fontSize = 12.sp)
        }
        when {
            locked -> Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Surface2).padding(6.dp)
            ) { Icon(Icons.Rounded.Lock, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(16.dp)) }
            selected -> Box(
                modifier = Modifier.size(26.dp).clip(CircleShape).background(Gradients.brandHorizontal),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
            format.isPro -> ProBadge()
        }
    }
}