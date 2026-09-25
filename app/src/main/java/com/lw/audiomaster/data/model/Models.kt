package com.lw.audiomaster.data.model

/** A single equalizer band. gainDb ranges -12f..12f. */
data class EqBand(
    val label: String,
    val hz: Int,
    val gainDb: Float = 0f
)

/** A named EQ curve (genre / voice preset). */
data class EqPreset(
    val id: String,
    val name: String,
    val emoji: String,
    val bands: List<Float>,   // gain for each of the 6 bands
    val isPro: Boolean = false
) {
    companion object {
        val BAND_LABELS = listOf("60", "230", "910", "3k", "6k", "14k")
        val BAND_HZ = listOf(60, 230, 910, 3600, 6000, 14000)

        val flat = EqPreset("flat", "Flat", "\uD83C\uDF9B", List(6) { 0f })

        val all: List<EqPreset> = listOf(
            flat,
            EqPreset("pop", "Pop", "\uD83C\uDFA4", listOf(2f, 1f, -1f, 2f, 3f, 2f)),
            EqPreset("rock", "Rock", "\uD83C\uDFB8", listOf(4f, 2f, -2f, 1f, 3f, 4f)),
            EqPreset("hiphop", "Hip Hop", "\uD83C\uDFA7", listOf(6f, 4f, 0f, -1f, 2f, 3f)),
            EqPreset("electronic", "Electronic", "\uD83C\uDF9A", listOf(5f, 3f, -2f, 1f, 4f, 5f), isPro = true),
            EqPreset("jazz", "Jazz", "\uD83C\uDFB7", listOf(3f, 2f, 1f, 2f, 1f, 2f), isPro = true),
            EqPreset("acoustic", "Acoustic", "\uD83C\uDFBC", listOf(2f, 1f, 2f, 3f, 2f, 1f)),
            EqPreset("podcast", "Podcast", "\uD83C\uDF99", listOf(-2f, 0f, 3f, 4f, 2f, -1f)),
            EqPreset("voiceover", "Voiceover", "\uD83D\uDDE3", listOf(-3f, 1f, 4f, 5f, 3f, 0f), isPro = true),
            EqPreset("bassboost", "Bass Boost", "\uD83D\uDD0A", listOf(8f, 5f, 0f, 0f, 1f, 2f), isPro = true),
            EqPreset("treble", "Treble Air", "\u2728", listOf(0f, -1f, 0f, 3f, 5f, 7f), isPro = true),
            EqPreset("warm", "Warm", "\uD83D\uDD25", listOf(4f, 3f, 2f, -1f, -2f, -1f), isPro = true)
        )
    }
}

enum class ExportFormat(val label: String, val ext: String, val isPro: Boolean) {
    MP3_320("MP3 · 320 kbps", "mp3", false),
    M4A_256("M4A · 256 kbps", "m4a", false),
    WAV_16("WAV · 16-bit", "wav", true),
    WAV_24("WAV · 24-bit", "wav", true),
    FLAC("FLAC · Lossless", "flac", true)
}

enum class MasterIntensity(val label: String, val gain: Float) {
    SUBTLE("Subtle", 0.35f),
    BALANCED("Balanced", 0.6f),
    LOUD("Loud", 0.82f),
    MAXIMUM("Maximum", 1.0f)
}

data class MasterSettings(
    val presetId: String = "flat",
    val bands: List<Float> = List(6) { 0f },
    val makeupGainDb: Float = 3f,
    val limiterCeilingDb: Float = -0.3f,
    val intensity: MasterIntensity = MasterIntensity.BALANCED,
    val stereoWidth: Float = 0.5f,
    val warmth: Float = 0.3f
)
