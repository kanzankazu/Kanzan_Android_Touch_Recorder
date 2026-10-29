package com.kanzan.core.ads

import android.app.Activity
import com.kanzan.core.domain.model.UserTier
import com.kanzan.core.domain.usecase.GetUserTierUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles ad trigger logic based on user tier and frequency
 */
@Singleton
class AdTrigger @Inject constructor(
    private val adManager: AdManager,
    private val getUserTierUseCase: GetUserTierUseCase
) {
    
    private var launchCount = 0
    
    /**
     * Trigger interstitial ad on app launch (every 3rd launch)
     */
    suspend fun onAppLaunch(activity: Activity) {
        if (shouldShowAd()) {
            launchCount++
            if (launchCount % 3 == 0) {
                adManager.showInterstitial(activity)
            }
        }
    }
    
    /**
     * Trigger interstitial ad after macro save
     */
    suspend fun onMacroSave(activity: Activity) {
        if (shouldShowAd()) {
            adManager.showInterstitial(activity)
        }
    }
    
    /**
     * Show rewarded ad to extend recording time
     */
    suspend fun showRewardedForRecording(
        activity: Activity,
        onRewarded: () -> Unit
    ) {
        adManager.showRewarded(activity, onRewarded = { onRewarded() })
    }
    
    private suspend fun shouldShowAd(): Boolean {
        val tier = getUserTierUseCase().first()
        return tier == UserTier.FREE
    }
}
