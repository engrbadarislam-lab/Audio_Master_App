package com.lw.audiomaster.ads

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.ui.components.GradientText
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.Surface2
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin

/**
 * Full-screen "Welcome Back" overlay, styled like the Home screen
 * (app background, brand-gradient icon tile, gradient title, SkyBlue loader).
 * When AppOpenAdManager flags a return-to-foreground, it covers the UI,
 * loads + shows the App Open ad, then disappears.
 */
@Composable
fun WelcomeBackHost(content: @Composable () -> Unit) {
    var showing by remember { mutableStateOf(false) }

    // Poll the manager's flag (it's set from a process-lifecycle callback)
    LaunchedEffect(Unit) {
        while (true) {
            if (AppOpenAdManager.welcomeBackRequested && !showing) {
                AppOpenAdManager.consumeWelcomeBackRequest()
                showing = true
                // Overlay is up; now load + show the ad (no preload)
                AppOpenAdManager.loadAndShow { showing = false }
                // Safety: never trap the user if the SDK stalls (~6s)
                var waited = 0
                while (showing && waited++ < 60) delay(100)
                showing = false
            }
            delay(200)
        }
    }

    Box(Modifier.fillMaxSize()) {
        content()

        AnimatedVisibility(
            visible = showing,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            WelcomeBackScreen()
        }
    }
}

@Composable
private fun WelcomeBackScreen() {
    // Same animated bars as the splash, in white on the brand tile
    val transition = rememberInfiniteTransition(label = "welcome")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Swallow taps so nothing underneath is clickable
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        ScreenBackground {
            Box(
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Glow + brand tile (like the onboarding / home hero)
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(Gradients.glow(0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(Gradients.brandVertical),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(Modifier.size(52.dp)) {
                                val bars = 5
                                val gap = 6f
                                val barW = (size.width - gap * (bars - 1)) / bars
                                val cy = size.height / 2f
                                for (i in 0 until bars) {
                                    val amp = abs(sin(phase + i * 0.7f)) * 0.75f + 0.2f
                                    val h = amp * size.height
                                    val x = i * (barW + gap) + barW / 2f
                                    drawLine(
                                        color = Color.White,
                                        start = Offset(x, cy - h / 2f),
                                        end = Offset(x, cy + h / 2f),
                                        strokeWidth = barW,
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                    GradientText("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("AudioMaster", color = TextSecondary, fontSize = 15.sp)
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp, vertical = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Loading…", color = TextMuted, fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        color = SkyBlue,
                        trackColor = Surface2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}