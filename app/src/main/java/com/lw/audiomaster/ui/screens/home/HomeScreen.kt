package com.lw.audiomaster.ui.screens.home

import android.app.Activity
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.play.core.review.ReviewManagerFactory
import com.lw.audiomaster.R
import com.lw.audiomaster.ads.NativeAdView
import com.lw.audiomaster.data.local.ProjectEntity
import com.lw.audiomaster.di.ServiceLocator
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.ui.util.findActivity
import com.lw.audiomaster.ui.components.CircleIconButton
import com.lw.audiomaster.ui.components.GlassCard
import com.lw.audiomaster.ui.components.GradientText
import com.lw.audiomaster.ui.components.ProBadge
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.components.SectionHeader
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.StrokeSoft
import com.lw.audiomaster.ui.theme.Surface1
import com.lw.audiomaster.ui.theme.Surface2
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/** True once Home has been shown this session (so the next time = user came back). */
private var homeShownBefore = false

/** Ask for a review at most once per app session. */
private var reviewAskedThisSession = false

@Composable
fun HomeScreen(
    vm: AppViewModel,
    onNewProject: () -> Unit,
    onQuickMaster: () -> Unit,
    onOpenPresets: () -> Unit,
    onOpenEq: () -> Unit,
    onOpenVolume: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenProject: (Long) -> Unit,
    onUpgrade: () -> Unit
) {
    val isPro by vm.isPro.collectAsState()
    val projects by vm.projects.collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Google in-app review: when the user navigates BACK to Home from any screen.
    LaunchedEffect(Unit) {
        if (!homeShownBefore) {
            homeShownBefore = true          // first time Home appears → not a "back"
        } else if (!reviewAskedThisSession) {
            reviewAskedThisSession = true
            delay(600)                      // let the back transition finish
            context.findActivity()?.let { launchInAppReview(it) }
        }
    }

    ScreenBackground {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.home_welcome), color = TextSecondary, fontSize = 13.sp)
                            Spacer(Modifier.height(2.dp))
                            GradientText("AudioMaster", fontSize = 26.sp)
                        }
                        CircleIconButton(Icons.Rounded.Settings, onClick = onOpenSettings, tint = TextSecondary)
                    }
                    Spacer(Modifier.height(20.dp))
                }

                if (!isPro) {
                    item {
                        ProUpsellCard(onUpgrade)
                        Spacer(Modifier.height(20.dp))
                    }
                }

                item {
                    // Hero "new master" card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Gradients.brandVertical)
                            .clickable(onClick = onNewProject)
                            .padding(20.dp)
                    ) {
                        Column {
                            Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                            Spacer(Modifier.height(14.dp))
                            Text(stringResource(R.string.home_master_new), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(stringResource(R.string.home_master_sub), color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                            Spacer(Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.18f))
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.action_start), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                item {
                    SectionHeader(stringResource(R.string.home_quick_tools))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickTool(Icons.Rounded.AutoAwesome, stringResource(R.string.tool_auto_master), Modifier.weight(1f), onQuickMaster)
                        QuickTool(Icons.Rounded.Equalizer, stringResource(R.string.tool_equalizer), Modifier.weight(1f), onOpenEq)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickTool(Icons.Rounded.Tune, stringResource(R.string.tool_presets), Modifier.weight(1f), onOpenPresets)
                        QuickTool(Icons.Rounded.VolumeUp, stringResource(R.string.tool_volume_boost), Modifier.weight(1f), onOpenVolume)
                    }
                    Spacer(Modifier.height(24.dp))
                }

                item {
                    SectionHeader(stringResource(R.string.home_recent), action = if (projects.isNotEmpty()) stringResource(R.string.home_see_all) else null, onAction = onOpenLibrary)
                }

                if (projects.isEmpty()) {
                    item { EmptyProjects(onNewProject) }
                } else {
                    items(projects.take(4), key = { it.id }) { p ->
                        ProjectRow(p, onClick = { onOpenProject(p.id) })
                        Spacer(Modifier.height(10.dp))
                    }
                }

                if (!isPro) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        NativeAdView("ads_home_native")
                    }
                }
            }

            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

/** Opens the Google Play in-app review dialog (Google decides if it actually shows). */
private fun launchInAppReview(activity: Activity) {
    val manager = ReviewManagerFactory.create(activity)
    manager.requestReviewFlow().addOnCompleteListener { task ->
        if (task.isSuccessful && !activity.isFinishing && !activity.isDestroyed) {
            manager.launchReviewFlow(activity, task.result)
        }
    }
}

@Composable
private fun ProUpsellCard(onUpgrade: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface2)
            .border(1.dp, StrokeSoft, RoundedCornerShape(20.dp))
            .clickable(onClick = onUpgrade)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(Gradients.brandVertical),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.go_pro), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(stringResource(R.string.go_pro_sub), color = TextSecondary, fontSize = 12.sp)
            }
            ProBadge(text = stringResource(R.string.badge_unlock))
        }
    }
}

@Composable
private fun QuickTool(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Gradients.card)
            .border(1.dp, StrokeSoft, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(Surface1),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(label, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1)
    }
}

@Composable
private fun EmptyProjects(onNewProject: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(Surface1),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.LibraryMusic, contentDescription = null, tint = TextMuted, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.home_no_projects), color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.home_no_projects_sub), color = TextMuted, fontSize = 12.sp)
        }
    }
}

@Composable
fun ProjectRow(p: ProjectEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface1)
            .border(1.dp, StrokeSoft, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(Gradients.brand)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(p.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, maxLines = 1)
            Spacer(Modifier.height(2.dp))
            Text("${p.presetName} · ${p.format}", color = TextMuted, fontSize = 12.sp, maxLines = 1)
        }
        if (p.exported) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Surface2).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(stringResource(R.string.exported), color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}