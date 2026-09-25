package com.lw.audiomaster.ui.screens.language

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.R
import com.lw.audiomaster.ui.components.GradientButton
import com.lw.audiomaster.ui.components.ScreenBackground
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.StrokeSoft
import com.lw.audiomaster.ui.theme.Surface1
import com.lw.audiomaster.ui.theme.Surface2
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary
import com.lw.audiomaster.ui.util.LocaleHelper
import com.lw.audiomaster.ui.util.findActivity

/** Shown once before onboarding so the user picks a language before entering the app. */
@Composable
fun LanguageSelectScreen(onChosen: (String) -> Unit) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(LocaleHelper.current(context).ifEmpty { "en" }) }

    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(20.dp)).background(Gradients.brandVertical),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Language, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.language_title), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.language_subtitle), color = TextSecondary, fontSize = 14.sp)
            Spacer(Modifier.height(20.dp))

            LazyColumn(Modifier.weight(1f)) {
                items(LANGS, key = { it.code }) { lang ->
                    val isSel = lang.code == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSel) Surface2 else Surface1)
                            .border(if (isSel) 2.dp else 1.dp, if (isSel) SkyBlue else StrokeSoft, RoundedCornerShape(16.dp))
                            .clickable { selected = lang.code }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(lang.flag, fontSize = 26.sp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(lang.native, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text(lang.english, color = TextMuted, fontSize = 12.sp)
                        }
                        if (isSel) {
                            Box(
                                modifier = Modifier.size(26.dp).clip(CircleShape).background(Gradients.brandHorizontal),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            GradientButton(
                text = stringResource(R.string.action_continue),
                onClick = {
                    LocaleHelper.persist(context, selected)
                    onChosen(selected)
                    // Recreate so the chosen locale applies to the whole app.
                    context.findActivity()?.recreate()
                }
            )
        }
    }
}

internal data class LangItem(val code: String, val native: String, val english: String, val flag: String)

internal val LANGS = listOf(
    LangItem("en", "English", "English", "🇺🇸"),
    LangItem("es", "Español", "Spanish", "🇪🇸"),
    LangItem("hi", "हिन्दी", "Hindi", "🇮🇳"),
    LangItem("fr", "Français", "French", "🇫🇷"),
    LangItem("de", "Deutsch", "German", "🇩🇪"),
    LangItem("ru", "Русский", "Russian", "🇷🇺")
)