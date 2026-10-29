package com.kanzan.core.data.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/**
 * DataStore extension for app preferences
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "kanzan_preferences"
)

/**
 * Preference keys for app settings
 */
object PreferencesKeys {
    val USER_TIER = stringPreferencesKey("user_tier")
    val ONBOARDING_SHOWN = booleanPreferencesKey("onboarding_shown")
    val AD_FREQUENCY = intPreferencesKey("ad_frequency")
}
