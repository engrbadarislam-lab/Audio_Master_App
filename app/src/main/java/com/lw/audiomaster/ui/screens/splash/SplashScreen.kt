package com.lw.audiomaster.ui.screens.splash

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.lw.audiomaster.ads.AdLoadingDialog
import com.lw.audiomaster.ads.AdManager
import com.lw.audiomaster.ads.ConsentManager
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.ElectricBlue
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin

private const val SPLASH_MIN_MS = 1600L

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val activity = LocalContext.current.findActivity()

    /** True while the splash interstitial is loading → shows AdLoadingDialog. */
    var adLoading by remember { mutableStateOf(false) }

    // ── Force-update state ──
    var updateCheckDone by remember { mutableStateOf(false) }
    var updateForced by remember { mutableStateOf(false) }

    val updateLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        // User dismissed a forced update → close the app
        if (result.resultCode != Activity.RESULT_OK) {
            activity?.finish()
        } else {
            updateForced = false
        }
    }

    LaunchedEffect(Unit) {
        if (activity == null) {
            delay(SPLASH_MIN_MS)
            onFinished()
            return@LaunchedEffect
        }

        // 1. Force-update check (runs in parallel with the splash animation).
        //    If Play has an IMMEDIATE update, show the Play update screen.
        //    The user cannot proceed — backing out of it closes the app.
        val manager = AppUpdateManagerFactory.create(activity)
        manager.appUpdateInfo
            .addOnSuccessListener { info ->
                val mustUpdate =
                    info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                            info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                if (mustUpdate) {
                    updateForced = true
                    manager.startUpdateFlowForResult(
                        info,
                        updateLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
                updateCheckDone = true
            }
            .addOnFailureListener {
                // Offline / sideloaded build → never trap the user
                updateCheckDone = true
            }

        // 2. Gather UMP consent up front (form shows only where required),
        //    in parallel with the splash animation.
        AdManager.ensureConsent(activity)

        // 3. Let the splash animation play.
        delay(SPLASH_MIN_MS)

        // 4. Wait for the update check + consent to settle, but never longer
        //    than ~6s, so a stalled SDK callback can't freeze the splash.
        var waited = 0
        while ((!updateCheckDone || !AdManager.consentResolved) && waited++ < 60) delay(100)

        // Consent form on screen → wait for the user's choice (NOT capped),
        // otherwise leaving the splash would destroy the form.
        while (ConsentManager.formShowing) delay(100)

        // Forced-update hold is intentional and NOT capped.
        while (updateForced) delay(100)

        // 5. Splash interstitial (skipped automatically for premium users
        //    inside AdManager), then continue.
        if (AdManager.canShowAds) {
            AdManager.showInterstitial(
                activity = activity,
                screenKey = "ads_splash_inter",
                fallbackScreenKey = "ads_splash_inter_fallback",
                onLoading = { adLoading = it },
                onDone = {
                    adLoading = false
                    onFinished()
                }
            )
        } else {
            onFinished()
        }
    }

    val transition = rememberInfiniteTransition(label = "splash")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )

    ScreenBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    val bars = 7
                    val gap = 10f
                    val barW = (size.width - gap * (bars - 1)) / bars
                    val cy = size.height / 2f
                    for (i in 0 until bars) {
                        val amp = abs(sin(phase + i * 0.7f)) * 0.75f + 0.2f
                        val h = amp * size.height
                        val x = i * (barW + gap)
                        drawLine(
                            brush = Brush.verticalGradient(listOf(CyanGlow, SkyBlue, ElectricBlue)),
                            start = Offset(x + barW / 2f, cy - h / 2f),
                            end = Offset(x + barW / 2f, cy + h / 2f),
                            strokeWidth = barW,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
            com.lw.audiomaster.ui.components.GradientText(
                text = "AudioMaster",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                brush = Brush.horizontalGradient(listOf(SkyBlue, CyanGlow))
            )
            Spacer(Modifier.height(8.dp))
            Text("Studio-grade mastering", color = TextSecondary, fontSize = 14.sp)
        }
    }

    if (adLoading) AdLoadingDialog()
}

/** Unwraps the Compose context to the hosting Activity. */
private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}