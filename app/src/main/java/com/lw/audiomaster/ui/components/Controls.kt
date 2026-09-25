package com.lw.audiomaster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lw.audiomaster.ui.theme.Abyss
import com.lw.audiomaster.ui.theme.CyanGlow
import com.lw.audiomaster.ui.theme.ElectricBlue
import com.lw.audiomaster.ui.theme.Gradients
import com.lw.audiomaster.ui.theme.SkyBlue
import com.lw.audiomaster.ui.theme.Surface1
import com.lw.audiomaster.ui.theme.Surface3
import com.lw.audiomaster.ui.theme.TextMuted
import com.lw.audiomaster.ui.theme.TextPrimary
import com.lw.audiomaster.ui.theme.TextSecondary

@Composable
fun LabeledSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueText: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Text(valueText ?: value.toInt().toString(), color = SkyBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = SkyBlue,
                activeTrackColor = ElectricBlue,
                inactiveTrackColor = Surface3
            )
        )
    }
}

@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface1)
            .padding(4.dp)
    ) {
        options.forEachIndexed { i, label ->
            val selected = i == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .then(
                        if (selected) Modifier.background(Gradients.brandHorizontal)
                        else Modifier
                    )
                    .clickable { onSelect(i) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    color = if (selected) Color.White else TextMuted,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun AppSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = ElectricBlue,
            uncheckedThumbColor = TextMuted,
            uncheckedTrackColor = Surface3,
            uncheckedBorderColor = Color.Transparent
        )
    )
}

@Composable
fun SettingRow(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(subtitle, color = TextMuted, fontSize = 12.sp)
            }
        }
        if (trailing != null) trailing()
    }
}
