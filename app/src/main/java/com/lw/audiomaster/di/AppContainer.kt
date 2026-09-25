package com.lw.audiomaster.di

import android.app.Activity
import android.content.Context
import com.lw.audiomaster.ads.onAdClick
import com.lw.audiomaster.data.analytics.Analytics
import com.lw.audiomaster.data.audio.AudioEngine
import com.lw.audiomaster.data.billing.BillingManager
import com.lw.audiomaster.data.billing.PremiumManager
import com.lw.audiomaster.data.config.ForceUpdateManager
import com.lw.audiomaster.data.local.AppDatabase
import com.lw.audiomaster.data.prefs.SettingsDataStore
import com.lw.audiomaster.data.repository.ProjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppContainer(context: Context) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val settings: SettingsDataStore = SettingsDataStore(context)

    private val db = AppDatabase.get(context)
    val projectRepository = ProjectRepository(db.projectDao())

    val audioEngine = AudioEngine()

    /** Fast Pro flag — now just the ONE premium flag (PremiumManager). */
    val isProNow: Boolean
        get() = PremiumManager.isPremium

    val forceUpdate = ForceUpdateManager()

    val billingManager: BillingManager = BillingManager(context) { isPro ->
        // PremiumManager is already updated inside BillingManager; keep DataStore in sync too.
        appScope.launch { settings.setPro(isPro) }
    }

    init {
        // Analytics + force-update gate come up with the app.
        Analytics.init(context)
        forceUpdate.start()
    }

    fun startBilling() = billingManager.start()

    // ---- Interstitial trigger ----

    /** Same logic as rememberAdClick (global counter + Remote Config + loading dialog). */
    fun registerClick(activity: Activity) = onAdClick(activity) {}
}