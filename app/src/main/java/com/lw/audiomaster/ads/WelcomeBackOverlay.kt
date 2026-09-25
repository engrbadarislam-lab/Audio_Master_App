package com.lw.audiomaster.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.delay

/**
 * Full-screen "Welcome Back" overlay, styled like the app's splash
 * (Two → One gradient, splash icon, white app name, white progress bar).
 * Wrap each activity's content with it. When AppOpenAdManager flags a
 * return-to-foreground, it covers the UI, loads + shows the App Open ad,
 * then disappears.
 */
@Composable
fun WelcomeBackHost(content: @Composable () -> Unit) {
    var showing by remember { mutableStateOf(false) }
    var requested by remember { mutableStateOf(false) }

    // Poll the manager's flag (it's set from a process-lifecycle callback)
    LaunchedEffect(Unit) {
        while (true) {
            if (AppOpenAdManager.welcomeBackRequested && !showing) {
                AppOpenAdManager.consumeWelcomeBackRequest()
                requested = true
            }
            delay(200)
        }
    }

    Box(Modifier.fillMaxSize()) {
        content()

        if (requested) {
            showing = true
            requested = false
            LaunchedEffect(Unit) {
                // Overlay is up; now load + show the ad (no preload)
                AppOpenAdManager.loadAndShow { showing = false }
                // Safety: never trap the user if the SDK stalls
                delay(6_000)
                showing = false
            }
        }

        if (showing) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Welcome Back",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2B4B)
                )
                Spacer(Modifier.height(20.dp))
                CircularProgressIndicator(color = Color(0xFF2D7CF6))
                Spacer(Modifier.height(16.dp))
                Text(
                    "Loading…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF5A6B87)
                )
            }
        }
    }
}