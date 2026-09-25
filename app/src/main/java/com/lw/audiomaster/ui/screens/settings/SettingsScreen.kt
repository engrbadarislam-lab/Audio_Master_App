package com.lw.audiomaster.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.di.ServiceLocator
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.R
import com.lw.audiomaster.ads.AdaptiveBannerAd
import com.lw.audiomaster.ui.components.AppSwitch
import com.lw.audiomaster.ui.components.AppTopBar
import com.lw.audiomaster.ui.util.LocaleHelper
import com.lw.audiomaster.ui.components.GlassCard
import com.lw.audiomaster.ui.components.ProBadge
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.components.SettingRow
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.StrokeSoft
import com.lw.audiomaster.ui.theme.Surface1
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onUpgrade: () -> Unit,
    onLanguage: () -> Unit,
    onAbout: () -> Unit,
    onHelp: () -> Unit
) {
    val isPro by vm.isPro.collectAsState()
    val settings = ServiceLocator.container.settings
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val haptics by settings.haptics.collectAsState(initial = true)
    val autoNormalize by settings.autoNormalize.collectAsState(initial = true)
    val keepOriginal by settings.keepOriginal.collectAsState(initial = true)
    val language by settings.language.collectAsState(initial = "en")

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppTopBar(title = stringResource(R.string.settings_title), onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                if (!isPro) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Gradients.brandVertical)
                            .clickable(onClick = onUpgrade)
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.upgrade_pro), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text(stringResource(R.string.upgrade_pro_sub), color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                        }
                        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.White)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Surface1)
                            .border(1.dp, StrokeSoft, RoundedCornerShape(20.dp))
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.pro_unlocked), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text(stringResource(R.string.pro_unlocked_sub), color = TextSecondary, fontSize = 12.sp)
                        }
                        ProBadge(text = stringResource(R.string.badge_active))
                    }
                }

                Spacer(Modifier.height(20.dp))
                SectionLabel(stringResource(R.string.section_audio))
                GlassCard(Modifier.fillMaxWidth()) {
                    SettingRow(
                        title = stringResource(R.string.set_auto_normalize),
                        subtitle = stringResource(R.string.set_auto_normalize_sub),
                        trailing = { AppSwitch(autoNormalize) { v -> scope.launch { settings.setAutoNormalize(v) } } }
                    )
                    Divider()
                    SettingRow(
                        title = stringResource(R.string.set_keep_original),
                        subtitle = stringResource(R.string.set_keep_original_sub),
                        trailing = { AppSwitch(keepOriginal) { v -> scope.launch { settings.setKeepOriginal(v) } } }
                    )
                    Divider()
                    SettingRow(
                        title = stringResource(R.string.set_haptics),
                        trailing = { AppSwitch(haptics) { v -> scope.launch { settings.setHaptics(v) } } }
                    )
                }

                Spacer(Modifier.height(20.dp))
                SectionLabel(stringResource(R.string.section_general))
                GlassCard(Modifier.fillMaxWidth()) {
                    NavRow(Icons.Rounded.Language, stringResource(R.string.nav_language), value = LocaleHelper.current(LocalContext.current).ifEmpty { "en" }.uppercase(), onClick = onLanguage)
                    Divider()
                    NavRow(Icons.Rounded.StarRate, stringResource(R.string.nav_rate), onClick = {
                        val pkg = context.packageName
                        val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg"))
                            .setPackage("com.android.vending")
                        val web = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
                        )
                        try {
                            context.startActivity(market)
                        } catch (e: Exception) {
                            runCatching { context.startActivity(web) }
                        }
                    })
                    Divider()
                    NavRow(Icons.Rounded.HelpOutline, stringResource(R.string.nav_help), onClick = onHelp)
                    Divider()
                    NavRow(Icons.Rounded.PrivacyTip, stringResource(R.string.nav_privacy), onClick = {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://logicworms.com/privacy-policy"))
                            )
                        }
                    })
                    Divider()
                    NavRow(Icons.Rounded.Info, stringResource(R.string.nav_about), onClick = onAbout)
                }

                Spacer(Modifier.height(24.dp))
                Text("AudioMaster v1.0.0", color = TextMuted, fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(Modifier.height(24.dp))
            }

            if (!isPro) AdaptiveBannerAd("ads_settings_banner")
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = SkyBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp, start = 4.dp))
}

@Composable
private fun Divider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(StrokeSoft))
}

@Composable
private fun NavRow(icon: ImageVector, title: String, value: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Text(title, color = TextPrimary, fontSize = 15.sp, modifier = Modifier.weight(1f))
        if (value != null) {
            Text(value, color = TextMuted, fontSize = 13.sp)
            Spacer(Modifier.width(6.dp))
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
    }
}