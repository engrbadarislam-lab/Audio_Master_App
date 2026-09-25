package com.lw.audiomaster.data.audio

import com.lw.audiomaster.data.model.MasterSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

/**
 * Placeholder audio engine. The UI is fully wired against this contract; swap the body
 * with a real decoder + DSP chain (e.g. Oboe/Superpowered or a native limiter) later.
 */
class AudioEngine {

    /** Deterministic pseudo-waveform so previews look stable per source. */
    fun waveform(seed: Long, bars: Int = 96): List<Float> {
        val rnd = Random(seed)
        return List(bars) { i ->
            val env = sin((i.toFloat() / bars) * Math.PI).toFloat()
            val jitter = rnd.nextFloat() * 0.55f
            (0.18f + abs(sin(i * 0.37f)) * 0.5f * env + jitter * env).coerceIn(0.05f, 1f)
        }
    }

    /** A slightly hotter, more even waveform to represent the mastered result. */
    fun masteredWaveform(source: List<Float>, settings: MasterSettings): List<Float> {
        val boost = 1f + settings.intensity.gain * 0.4f
        return source.map { (it * boost).coerceAtMost(1f) }
            .map { 0.35f + it * 0.65f }  // limiter raises the noise floor / evens peaks
    }

    /** Emits 0f..1f progress while "processing". */
    fun process(settings: MasterSettings): Flow<Float> = flow {
        var p = 0f
        while (p < 1f) {
            delay(90)
            p += 0.06f + Random.nextFloat() * 0.05f
            emit(p.coerceAtMost(1f))
        }
        emit(1f)
    }

    /** Fake loudness readout in LUFS for the A/B panel. */
    fun estimateLufs(settings: MasterSettings): Float {
        val base = -18f
        return (base + settings.intensity.gain * 9f + settings.makeupGainDb * 0.3f)
            .coerceIn(-24f, -7f)
    }
}
