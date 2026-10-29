package com.kanzan.core.domain.usecase

import com.kanzan.core.domain.model.AutoClickConfig
import com.kanzan.core.domain.repository.AutoClickRepository
import javax.inject.Inject

/**
 * Use case for saving an auto-click configuration
 */
class SaveAutoClickConfigUseCase @Inject constructor(
    private val autoClickRepository: AutoClickRepository
) {
    
    /**
     * Save a new or updated auto-click config
     * 
     * @param config Config to save
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(config: AutoClickConfig): Result<Unit> {
        return autoClickRepository.saveConfig(config)
    }
}
