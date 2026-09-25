package com.lw.audiomaster.ui.screens.paywall

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.data.billing.PlanOption
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.ui.components.CircleIconButton
import com.lw.audiomaster.ui.components.GradientButton
import com.lw.audiomaster.ui.components.GradientText
import com.lw.audiomaster.R
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.util.findActivity
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.StrokeSoft
import com.lw.audiomaster.ui.theme.Surface1
import com.lw.audiomaster.ui.theme.Surface2
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary

private val benefitRes = listOf(
    R.string.benefit_no_ads,
    R.string.benefit_presets,
    R.string.benefit_export,
    R.string.benefit_loudness,
    R.string.benefit_unlimited
)

@Composable
fun PaywallScreen(vm: AppViewModel, onClose: () -> Unit) {
    val plans by vm.plans.collectAsState()
    var selected by remember { mutableStateOf(0) }
    val context = LocalContext.current

    // Fallback display plans if Play hasn't populated yet (e.g. debug / no account)
    val displayPlans = if (plans.isNotEmpty()) plans else listOf(
        PlanOption("audiomaster_pro", "m", "Monthly", "$4.99", "per month"),
        PlanOption("audiomaster_pro", "y", "Yearly", "$29.99", "per year", badge = "BEST VALUE"),
        PlanOption("audiomaster_lifetime", null, "Lifetime", "$59.99", "one-time", badge = "PAY ONCE", isLifetime = true)
    )

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.End) {
                CircleIconButton(icon = Icons.Rounded.Close, onClick = onClose, tint = TextSecondary)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Gradients.brandVertical),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(38.dp))
                }
                Spacer(Modifier.height(14.dp))
                GradientText(stringResource(R.string.pro_title), fontSize = 26.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.pro_subtitle),
                    color = TextSecondary, fontSize = 14.sp, lineHeight = 20.sp
                )
                Spacer(Modifier.height(18.dp))

                benefitRes.forEach { b ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(b), color = TextPrimary, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(18.dp))

                displayPlans.forEachIndexed { i, plan ->
                    PlanRow(plan = plan, selected = i == selected, onClick = { selected = i })
                    Spacer(Modifier.height(12.dp))
                }
                Spacer(Modifier.height(8.dp))
            }

            Column(Modifier.padding(24.dp)) {
                GradientButton(
                    text = stringResource(R.string.action_continue),
                    onClick = {
                        val activity = context.findActivity()
                        val chosen = displayPlans.getOrNull(selected)
                        if (activity != null && plans.isNotEmpty() && chosen != null) {
                            // Real Play Billing flow for the selected tier.
                            vm.purchase(activity, chosen)
                        }
                        // If there's no Play data (debug / emulator), we simply close —
                        // we do NOT fake a Pro unlock, otherwise all ads would be hidden
                        // while testing. Test Pro via a real Play internal-testing track.
                        onClose()
                    }
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.pro_legal),
                    color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(), lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun PlanRow(plan: PlanOption, selected: Boolean, onClick: () -> Unit) {
    val border = if (selected) SkyBlue else StrokeSoft
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) Surface2 else Surface1)
            .border(if (selected) 2.dp else 1.dp, border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (selected) Brush.linearGradient(listOf(SkyBlue, CyanGlow)) else Brush.linearGradient(listOf(Surface2, Surface2)))
                    .border(1.dp, if (selected) Color.Transparent else TextMuted, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(plan.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (plan.badge != null) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Gradients.brandHorizontal)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(plan.badge, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(plan.period, color = TextMuted, fontSize = 12.sp)
            }
            Text(plan.price, color = SkyBlue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}
