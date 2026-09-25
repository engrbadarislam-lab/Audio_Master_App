package com.lw.audiomaster.ui.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Per-app language handling. The chosen tag is stored in SharedPreferences so it can be
 * read synchronously from Activity.attachBaseContext (DataStore is async and too late there).
 */
object LocaleHelper {
    private const val PREF = "audiomaster_locale"
    private const val KEY = "lang"

    /** Supported UI languages (base English + the five shipped translations). */
    val supported = listOf("en", "es", "hi", "fr", "de", "ru")

    fun persist(context: Context, lang: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY, lang).apply()
    }

    /** Current chosen tag, or "" if the user hasn't chosen yet (first run). */
    fun current(context: Context): String =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, "").orEmpty()

    fun hasChosen(context: Context): Boolean = current(context).isNotEmpty()

    /** Wrap a base context in the chosen locale. Returns the context unchanged on first run. */
    fun wrap(context: Context): Context {
        val lang = current(context)
        if (lang.isEmpty()) return context
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
