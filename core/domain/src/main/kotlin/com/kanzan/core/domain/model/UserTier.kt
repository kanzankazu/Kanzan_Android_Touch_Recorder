package com.kanzan.core.domain.model

/**
 * User subscription tier
 */
enum class UserTier {
    /** Free tier with limitations and ads */
    FREE,
    
    /** Premium tier with unlimited features and no ads */
    PREMIUM
}

/**
 * Centralized free tier limits for consistency
 * 
 * @property maxRecordingDuration Maximum macro recording duration in ms (30 seconds)
 * @property maxAutoClickCount Maximum auto-click count per session
 * @property maxMultiTargets Maximum number of multi-click targets
 * @property maxMacroStorage Maximum number of saved macros
 */
data class FreeTierLimits(
    val maxRecordingDuration: Long = 30_000, // 30 seconds
    val maxAutoClickCount: Int = 100,
    val maxMultiTargets: Int = 3,
    val maxMacroStorage: Int = 5
)
