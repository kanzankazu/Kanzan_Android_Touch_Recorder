package com.kanzan.core.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages AdMob interstitial and rewarded ads
 */
@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    
    init {
        MobileAds.initialize(context)
        loadInterstitialAd()
        loadRewardedAd()
    }
    
    // Interstitial Ad
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            AD_UNIT_INTERSTITIAL,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }
    
    fun showInterstitial(activity: Activity, onAdClosed: () -> Unit = {}) {
        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    loadInterstitialAd() // Preload next
                    onAdClosed()
                }
                
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    loadInterstitialAd()
                    onAdClosed()
                }
            }
            ad.show(activity)
        } ?: run {
            loadInterstitialAd()
            onAdClosed()
        }
    }
    
    // Rewarded Ad
    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        
        RewardedAd.load(
            context,
            AD_UNIT_REWARDED,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }
    
    fun showRewarded(
        activity: Activity,
        onRewarded: (Int) -> Unit,
        onAdClosed: () -> Unit = {}
    ) {
        rewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    loadRewardedAd() // Preload next
                    onAdClosed()
                }
                
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    loadRewardedAd()
                    onAdClosed()
                }
            }
            
            ad.show(activity) { rewardItem ->
                onRewarded(rewardItem.amount)
            }
        } ?: run {
            loadRewardedAd()
            onAdClosed()
        }
    }
    
    companion object {
        // Test IDs - Replace with real IDs in production
        private const val AD_UNIT_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
        private const val AD_UNIT_REWARDED = "ca-app-pub-3940256099942544/5224354917"
    }
}
