package com.lw.audiomaster.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lw.audiomaster.data.audio.AudioEngine
import com.lw.audiomaster.data.billing.BillingManager
import com.lw.audiomaster.data.model.EqPreset
import com.lw.audiomaster.data.model.ExportFormat
import com.lw.audiomaster.data.model.MasterIntensity
import com.lw.audiomaster.data.model.MasterSettings
import com.lw.audiomaster.data.local.ProjectEntity
import com.lw.audiomaster.data.prefs.SettingsDataStore
import com.lw.audiomaster.data.repository.ProjectRepository
import com.lw.audiomaster.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Represents the audio currently loaded for mastering. */
data class AudioSource(
    val name: String,
    val durationMs: Long,
    val seed: Long,
    val isVideo: Boolean = false
)

class AppViewModel(
    private val settings: SettingsDataStore,
    private val engine: AudioEngine,
    private val repo: ProjectRepository,
    private val billing: BillingManager
) : ViewModel() {

    /** The ONE premium flag — same as PremiumManager (ads) via BillingManager. */
    val isPro: StateFlow<Boolean> = billing.isPro

    val onboarded: StateFlow<Boolean> =
        settings.onboarded.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Billing surface
    val plans = billing.plans
    val billingState = billing.state

    // ---- Session ----
    private val _source = MutableStateFlow<AudioSource?>(null)
    val source: StateFlow<AudioSource?> = _source.asStateFlow()

    private val _settingsState = MutableStateFlow(MasterSettings())
    val master: StateFlow<MasterSettings> = _settingsState.asStateFlow()

    private val _waveform = MutableStateFlow<List<Float>>(emptyList())
    val waveform: StateFlow<List<Float>> = _waveform.asStateFlow()

    private val _mastered = MutableStateFlow<List<Float>>(emptyList())
    val mastered: StateFlow<List<Float>> = _mastered.asStateFlow()

    private val _exportFormat = MutableStateFlow(ExportFormat.MP3_320)
    val exportFormat: StateFlow<ExportFormat> = _exportFormat.asStateFlow()

    val projects = repo.observeAll()

    fun loadSource(source: AudioSource) {
        _source.value = source
        val wave = engine.waveform(source.seed)
        _waveform.value = wave
        recomputeMastered()
    }

    /** For demoing when no picker result is available. */
    fun loadDemoSource(name: String = "Demo Track.wav", isVideo: Boolean = false) {
        loadSource(AudioSource(name, durationMs = 184_000, seed = name.hashCode().toLong(), isVideo = isVideo))
    }

    /** Ensure there is a source loaded before opening a tool screen. */
    fun ensureSource() {
        if (_source.value == null) loadDemoSource()
    }

    private fun recomputeMastered() {
        val wave = _waveform.value
        if (wave.isNotEmpty()) _mastered.value = engine.masteredWaveform(wave, _settingsState.value)
    }

    fun applyPreset(preset: EqPreset) {
        _settingsState.value = _settingsState.value.copy(presetId = preset.id, bands = preset.bands)
        recomputeMastered()
    }

    fun setBand(index: Int, value: Float) {
        val bands = _settingsState.value.bands.toMutableList()
        if (index in bands.indices) {
            bands[index] = value
            _settingsState.value = _settingsState.value.copy(bands = bands, presetId = "custom")
            recomputeMastered()
        }
    }

    fun setMakeupGain(db: Float) {
        _settingsState.value = _settingsState.value.copy(makeupGainDb = db); recomputeMastered()
    }

    fun setCeiling(db: Float) {
        _settingsState.value = _settingsState.value.copy(limiterCeilingDb = db)
    }

    fun setIntensity(i: MasterIntensity) {
        _settingsState.value = _settingsState.value.copy(intensity = i); recomputeMastered()
    }

    fun setStereoWidth(v: Float) { _settingsState.value = _settingsState.value.copy(stereoWidth = v) }
    fun setWarmth(v: Float) { _settingsState.value = _settingsState.value.copy(warmth = v) }
    fun setExportFormat(f: ExportFormat) { _exportFormat.value = f }

    fun estimatedLufs(): Float = engine.estimateLufs(_settingsState.value)
    fun processFlow() = engine.process(_settingsState.value)

    fun completeOnboarding() = viewModelScope.launch { settings.setOnboarded(true) }

    fun purchaseDemoUnlock() = billing.grantProForDemo()

    /** Launch the real Play Billing purchase flow for the chosen plan. */
    fun purchase(activity: android.app.Activity, option: com.lw.audiomaster.data.billing.PlanOption) =
        billing.purchase(activity, option)

    fun saveCurrentProject(exported: Boolean, outputPath: String? = null) {
        val src = _source.value ?: return
        val s = _settingsState.value
        val presetName = EqPreset.all.firstOrNull { it.id == s.presetId }?.name ?: "Custom"
        viewModelScope.launch {
            repo.save(
                ProjectEntity(
                    title = src.name.substringBeforeLast('.'),
                    sourceName = src.name,
                    durationMs = src.durationMs,
                    presetId = s.presetId,
                    presetName = presetName,
                    makeupGainDb = s.makeupGainDb,
                    format = _exportFormat.value.label,
                    exported = exported,
                    outputPath = outputPath
                )
            )
        }
    }

    fun deleteProject(p: ProjectEntity) = viewModelScope.launch { repo.delete(p) }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val c = ServiceLocator.container
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(c.settings, c.audioEngine, c.projectRepository, c.billingManager) as T
        }
    }
}