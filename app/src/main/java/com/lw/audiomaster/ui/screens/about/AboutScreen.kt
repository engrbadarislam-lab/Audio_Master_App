package com.lw.audiomaster.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.ui.components.AppTopBar
import com.lw.audiomaster.ui.components.GlassCard
import com.lw.audiomaster.ui.components.GradientText
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary

@Composable
fun AboutScreen(onBack: () -> Unit) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppTopBar(title = "About", onBack = onBack)
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier.size(96.dp).clip(RoundedCornerShape(28.dp)).background(Gradients.brandVertical),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
                }
                Spacer(Modifier.height(18.dp))
                GradientText("AudioMaster", fontSize = 26.sp)
                Spacer(Modifier.height(6.dp))
                Text("Version 1.0.0", color = TextMuted, fontSize = 13.sp)
                Spacer(Modifier.height(24.dp))
                GlassCard(Modifier.fillMaxWidth()) {
                    Text(
                        "AudioMaster brings studio-grade mastering to your pocket. Boost loudness, " +
                            "shape your tone with a 6-band EQ, and export release-ready audio for music, " +
                            "podcasts and video — all without ever overwriting your originals.",
                        color = TextSecondary, fontSize = 14.sp, lineHeight = 21.sp, textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.weight(1f))
                Text("Made with care by Logic Worms", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text("© 2026 Logic Worms Pvt Ltd", color = TextMuted, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
