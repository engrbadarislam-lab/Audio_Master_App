package com.lw.audiomaster.ui.screens.library

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.ads.AdaptiveBannerAd
import com.lw.audiomaster.ui.AppViewModel
import com.lw.audiomaster.ui.components.AppTopBar
import com.lw.audiomaster.ui.components.CircleIconButton
import com.lw.audiomaster.ui.screens.home.ProjectRow
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.theme.Surface1
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary

@Composable
fun LibraryScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onNewProject: () -> Unit,
    onOpenProject: (Long) -> Unit
) {
    val projects by vm.projects.collectAsState(initial = emptyList())
    val isPro by vm.isPro.collectAsState()

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppTopBar(
                title = "My Library",
                onBack = onBack,
                trailing = { CircleIconButton(Icons.Rounded.Add, onClick = onNewProject) }
            )

            Box(Modifier.weight(1f).fillMaxWidth()) {
                if (projects.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp).clip(CircleShape).background(Surface1),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.LibraryMusic, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Your library is empty", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("Mastered projects will appear here", color = TextMuted, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
                    ) {
                        item {
                            Text("${projects.size} project${if (projects.size == 1) "" else "s"}", color = TextMuted, fontSize = 13.sp)
                            Spacer(Modifier.height(12.dp))
                        }
                        items(projects, key = { it.id }) { p ->
                            ProjectRow(p, onClick = { onOpenProject(p.id) })
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }

            if (!isPro) AdaptiveBannerAd("ads_libraryscreen_banner")
        }
    }
}
