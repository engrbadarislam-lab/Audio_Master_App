package com.lw.audiomaster.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Gradients {
    val brand = listOf(ElectricBlue, SkyBlue, CyanGlow)
    val brandSweep = listOf(ElectricBlue, SkyBlue, CyanGlow, SkyBlue, ElectricBlue)

    val screenBackground = Brush.verticalGradient(
        colors = listOf(Abyss, DeepNavy, MidnightBlue)
    )

    val screenBackgroundRadial = Brush.radialGradient(
        colors = listOf(OceanBlue, DeepNavy, Abyss),
        center = Offset(540f, 0f),
        radius = 1400f
    )

    val card = Brush.verticalGradient(colors = listOf(Surface2, Surface1))

    val cardHighlight = Brush.linearGradient(
        colors = listOf(Surface3, Surface1)
    )

    val brandHorizontal = Brush.horizontalGradient(colors = brand)

    val brandVertical = Brush.verticalGradient(colors = brand)

    fun glow(alpha: Float = 0.35f) = Brush.radialGradient(
        colors = listOf(SkyBlue.copy(alpha = alpha), Color.Transparent)
    )
}
