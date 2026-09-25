package com.lw.audiomaster.data.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.lw.audiomaster.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Force-update gate. Reads `min_version_code` from Firebase Remote Config and flips
 * [updateRequired] to true when it exceeds this build's versionCode. The UI shows a
 * non-dismissible blocking screen while that flag is set.
 *
 * Set `min_version_code` in the Firebase console to the lowest versionCode you still
 * allow; leave it at/below the shipped versionCode to keep everyone unblocked.
 */
class ForceUpdateManager {

    private val _updateRequired = MutableStateFlow(false)
    val updateRequired: StateFlow<Boolean> = _updateRequired.asStateFlow()

    fun start() {
        val rc = FirebaseRemoteConfig.getInstance()
        rc.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0L else 3600L)
                .build()
        )
        rc.setDefaultsAsync(mapOf(KEY to 0L))
        rc.fetchAndActivate().addOnCompleteListener {
            val min = rc.getLong(KEY)
            _updateRequired.value = min > BuildConfig.VERSION_CODE.toLong()
        }
    }

    private companion object {
        const val KEY = "min_version_code"
    }
}
