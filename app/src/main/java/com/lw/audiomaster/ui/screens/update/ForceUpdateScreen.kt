package com.lw.audiomaster.ui.screens.update

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.ui.components.GradientButton
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary

/** Full-screen, non-dismissible gate shown when a newer version is required. */
@Composable
fun ForceUpdateScreen() {
    val context = LocalContext.current

    // Swallow the back button so the block can't be dismissed.
    BackHandler(enabled = true) { }

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(96.dp).clip(RoundedCornerShape(28.dp)).background(Gradients.brandVertical),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.SystemUpdate, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text("Update required", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(Modifier.height(10.dp))
            Text(
                "A newer version of AudioMaster is available. Please update to keep using the app.",
                color = TextSecondary, fontSize = 15.sp, lineHeight = 22.sp, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))
            GradientButton(
                text = "Update now",
                onClick = {
                    val pkg = context.packageName
                    val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg"))
                    val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$pkg"))
                    try {
                        context.startActivity(market)
                    } catch (e: Exception) {
                        context.startActivity(web)
                    }
                }
            )
        }
    }
}
