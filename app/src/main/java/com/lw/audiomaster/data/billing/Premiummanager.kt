package com.lw.audiomaster.data.billing
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val PREFS = "premium_prefs"
private const val KEY_PREMIUM = "is_premium"

/** Saved premium flag (SharedPreferences). */
var Context.isPremium: Boolean
    get() = getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_PREMIUM, false)
    set(value) = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit().putBoolean(KEY_PREMIUM, value).apply()

/**
 * The ONE premium flag. BillingManager writes it on purchase/restore;
 * AdManager reads it to stop ads.
 */
object PremiumManager {

    private var appContext: Context? = null

    private val _isPremium = MutableStateFlow(false)
    val isPremiumFlow: StateFlow<Boolean> = _isPremium

    fun init(context: Context) {
        if (appContext != null) return
        appContext = context.applicationContext
        _isPremium.value = context.applicationContext.isPremium
    }

    val isPremium: Boolean
        get() = _isPremium.value

    fun setPremium(value: Boolean) {
        appContext?.isPremium = value
        _isPremium.value = value
    }
}