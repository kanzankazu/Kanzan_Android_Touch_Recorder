package com.kanzan.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.kanzan.core.data.util.PreferencesKeys
import com.kanzan.core.domain.model.UserTier
import com.kanzan.core.domain.repository.SettingsRepository
import com.kanzan.core.service.util.AccessibilityHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for app settings using DataStore
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    
    override fun getUserTier(): Flow<UserTier> {
        return dataStore.data.map { preferences ->
            val tierString = preferences[PreferencesKeys.USER_TIER] ?: UserTier.FREE.name
            try {
                UserTier.valueOf(tierString)
            } catch (e: IllegalArgumentException) {
                UserTier.FREE
            }
        }
    }
    
    override suspend fun setUserTier(tier: UserTier) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_TIER] = tier.name
        }
    }
    
    override fun isAccessibilityEnabled(): Flow<Boolean> {
        return AccessibilityHelper.observeAccessibilityStatus(context)
    }
    
    override fun hasShownOnboarding(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_SHOWN] ?: false
        }
    }
    
    override suspend fun setOnboardingShown(shown: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_SHOWN] = shown
        }
    }
    
    override fun getAdFrequency(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.AD_FREQUENCY] ?: 0
        }
    }
    
    override suspend fun incrementAdFrequency() {
        dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.AD_FREQUENCY] ?: 0
            preferences[PreferencesKeys.AD_FREQUENCY] = current + 1
        }
    }
}
