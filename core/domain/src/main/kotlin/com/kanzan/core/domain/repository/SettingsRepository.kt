package com.kanzan.core.domain.repository

import com.kanzan.core.domain.model.UserTier
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for app settings and preferences
 */
interface SettingsRepository {
    
    /**
     * Get current user tier as reactive flow
     * @return Flow emitting user tier
     */
    fun getUserTier(): Flow<UserTier>
    
    /**
     * Update user tier
     * @param tier New user tier
     */
    suspend fun setUserTier(tier: UserTier)
    
    /**
     * Check if accessibility service is enabled
     * @return Flow emitting accessibility status
     */
    fun isAccessibilityEnabled(): Flow<Boolean>
    
    /**
     * Check if onboarding has been shown
     * @return Flow emitting onboarding status
     */
    fun hasShownOnboarding(): Flow<Boolean>
    
    /**
     * Mark onboarding as shown
     * @param shown Whether onboarding has been completed
     */
    suspend fun setOnboardingShown(shown: Boolean)
    
    /**
     * Get ad frequency counter (for interstitial trigger logic)
     * @return Flow emitting launch count
     */
    fun getAdFrequency(): Flow<Int>
    
    /**
     * Increment ad frequency counter
     */
    suspend fun incrementAdFrequency()
}
