package com.kanzan.core.domain.usecase

import com.kanzan.core.domain.model.AutoClickConfig
import com.kanzan.core.domain.model.FreeTierLimits
import com.kanzan.core.domain.model.UserTier
import com.kanzan.core.domain.repository.AutoClickRepository
import com.kanzan.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for starting auto-click execution
 * 
 * Validates free tier limits before enabling
 */
class StartAutoClickUseCase @Inject constructor(
    private val autoClickRepository: AutoClickRepository,
    private val settingsRepository: SettingsRepository
) {
    
    /**
     * Start auto-click with the given config
     * 
     * @param configId Auto-click config ID
     * @return Result with enabled config or error
     */
    suspend operator fun invoke(configId: String): Result<AutoClickConfig> {
        val config = autoClickRepository.getConfigById(configId)
            ?: return Result.failure(Exception("Config not found"))
        
        val tier = settingsRepository.getUserTier().first()
        
        // Free tier validation
        if (tier == UserTier.FREE) {
            val limits = FreeTierLimits()
            
            // Check click count limit
            if (config.repeatCount > limits.maxAutoClickCount && config.repeatCount != -1) {
                return Result.failure(
                    Exception("Click count exceeds free tier limit of ${limits.maxAutoClickCount}")
                )
            }
            
            // Check multi-target limit
            if (config.targets.size > limits.maxMultiTargets) {
                return Result.failure(
                    Exception("Multi-target count exceeds free tier limit of ${limits.maxMultiTargets}")
                )
            }
            
            // Disallow unlimited clicks in free tier
            if (config.repeatCount == -1) {
                return Result.failure(
                    Exception("Unlimited clicks available in Premium only")
                )
            }
        }
        
        val updatedConfig = config.copy(isEnabled = true)
        autoClickRepository.updateConfig(updatedConfig)
        
        return Result.success(updatedConfig)
    }
}
