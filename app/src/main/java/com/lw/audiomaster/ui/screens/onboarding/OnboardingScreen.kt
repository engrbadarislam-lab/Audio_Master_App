package com.lw.audiomaster.ui.screens.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.R
import com.lw.audiomaster.ui.components.GradientButton
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.Surface3
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.systemBarsPadding
import com.lw.audiomaster.ads.AdaptiveBannerAd
import kotlinx.coroutines.launch

private data class OnbPage(val icon: ImageVector, val titleRes: Int, val bodyRes: Int)

private val pages = listOf(
    OnbPage(Icons.Rounded.GraphicEq, R.string.onb1_title, R.string.onb1_body),
    OnbPage(Icons.Rounded.Equalizer, R.string.onb2_title, R.string.onb2_body),
    OnbPage(Icons.Rounded.Speed, R.string.onb3_title, R.string.onb3_body),
    OnbPage(Icons.Rounded.IosShare, R.string.onb4_title, R.string.onb4_body)
)

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Content (padded)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        stringResource(R.string.action_skip),
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDone() }
                            .padding(8.dp),
                    )
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { index ->
                    val page = pages[index]
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
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
                                Icon(page.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                            }
                        }
                        Spacer(Modifier.height(40.dp))
                        Text(stringResource(page.titleRes), color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(14.dp))
                        Text(
                            stringResource(page.bodyRes),
                            color = TextSecondary,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pages.size) { i ->
                        val selected = pagerState.currentPage == i
                        val w by animateDpAsState(if (selected) 26.dp else 8.dp, label = "dot")
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(w)
                                .clip(CircleShape)
                                .background(if (selected) Brush.horizontalGradient(listOf(SkyBlue, CyanGlow)) else Brush.horizontalGradient(listOf(Surface3, Surface3)))
                        )
                    }
                }

                val isLast = pagerState.currentPage == pages.size - 1
                GradientButton(
                    text = if (isLast) stringResource(R.string.action_get_started) else stringResource(R.string.action_continue),
                    onClick = {
                        if (isLast) onDone()
                        else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                )
            }

            // Full-width banner at the bottom of the screen
            AdaptiveBannerAd("ads_onboarding_banner")
        }
    }
}