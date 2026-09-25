package com.lw.audiomaster.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val ONBOARDED = booleanPreferencesKey("onboarded")
        val PRO = booleanPreferencesKey("is_pro")
        val LANGUAGE = stringPreferencesKey("language")
        val HAPTICS = booleanPreferencesKey("haptics")
        val AUTO_NORMALIZE = booleanPreferencesKey("auto_normalize")
        val KEEP_ORIGINAL = booleanPreferencesKey("keep_original")
    }

    val onboarded: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDED] ?: false }
    val isPro: Flow<Boolean> = context.dataStore.data.map { it[Keys.PRO] ?: false }
    val language: Flow<String> = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "en" }
    val haptics: Flow<Boolean> = context.dataStore.data.map { it[Keys.HAPTICS] ?: true }
    val autoNormalize: Flow<Boolean> = context.dataStore.data.map { it[Keys.AUTO_NORMALIZE] ?: true }
    val keepOriginal: Flow<Boolean> = context.dataStore.data.map { it[Keys.KEEP_ORIGINAL] ?: true }

    suspend fun setOnboarded(v: Boolean) = context.dataStore.edit { it[Keys.ONBOARDED] = v }
    suspend fun setPro(v: Boolean) = context.dataStore.edit { it[Keys.PRO] = v }
    suspend fun setLanguage(v: String) = context.dataStore.edit { it[Keys.LANGUAGE] = v }
    suspend fun setHaptics(v: Boolean) = context.dataStore.edit { it[Keys.HAPTICS] = v }
    suspend fun setAutoNormalize(v: Boolean) = context.dataStore.edit { it[Keys.AUTO_NORMALIZE] = v }
    suspend fun setKeepOriginal(v: Boolean) = context.dataStore.edit { it[Keys.KEEP_ORIGINAL] = v }
}
