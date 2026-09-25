package com.lw.audiomaster.ui.screens.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.ui.components.AppTopBar
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.StrokeSoft
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary

private data class Faq(val q: String, val a: String)

private val faqs = listOf(
    Faq("What is mastering?", "Mastering is the final polish on your audio — balancing frequencies, boosting loudness and controlling peaks so your track sounds clear and competitive on every device."),
    Faq("Will my original file be changed?", "Never. AudioMaster always exports a new file and keeps your original untouched."),
    Faq("What formats can I import?", "Any common audio or video file: WAV, MP3, M4A, AAC, FLAC, MP4 and MOV. For video, only the audio is processed."),
    Faq("How does the limiter prevent clipping?", "The limiter sets a maximum peak ceiling. Volume is raised toward that ceiling, and anything that would exceed it is smoothly caught instead of distorting."),
    Faq("What's the difference between presets?", "Each preset applies an EQ curve tuned for a genre or voice — Podcast lifts speech clarity, Hip Hop pushes low-end punch, and so on. You can always fine-tune afterwards."),
    Faq("What do I get with Pro?", "Pro unlocks all genre & voice presets, lossless WAV/FLAC and 24-bit export, the Maximum loudness mode, and stereo widening plus analog warmth.")
)

@Composable
fun HelpScreen(onBack: () -> Unit) {
    var expanded by remember { mutableIntStateOf(-1) }

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppTopBar(title = "Help & FAQ", onBack = onBack)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
            ) {
                itemsIndexed(faqs) { index, faq ->
                    val open = expanded == index
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Gradients.card)
                            .border(1.dp, StrokeSoft, RoundedCornerShape(16.dp))
                            .clickable { expanded = if (open) -1 else index }
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(faq.q, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                            Spacer(Modifier.size(8.dp))
                            Icon(
                                if (open) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                contentDescription = null, tint = SkyBlue
                            )
                        }
                        AnimatedVisibility(visible = open) {
                            Column {
                                Spacer(Modifier.height(10.dp))
                                Text(faq.a, color = TextSecondary, fontSize = 14.sp, lineHeight = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
