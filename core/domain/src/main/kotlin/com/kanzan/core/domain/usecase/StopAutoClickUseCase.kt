package com.kanzan.core.domain.usecase

import com.kanzan.core.domain.repository.AutoClickRepository
import javax.inject.Inject

/**
 * Use case for stopping auto-click execution
 */
class StopAutoClickUseCase @Inject constructor(
    private val autoClickRepository: AutoClickRepository
) {
    
    /**
     * Stop auto-click execution
     * 
     * @param configId Auto-click config ID
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(configId: String): Result<Unit> {
        val config = autoClickRepository.getConfigById(configId)
            ?: return Result.failure(Exception("Config not found"))
        
        val updatedConfig = config.copy(isEnabled = false)
        return autoClickRepository.updateConfig(updatedConfig).map { }
    }
}
