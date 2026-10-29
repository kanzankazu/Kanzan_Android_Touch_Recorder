package com.kanzan.core.domain.usecase

import com.kanzan.core.domain.model.UserTier
import com.kanzan.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving current user tier
 */
class GetUserTierUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    
    /**
     * Get user tier as reactive flow
     * 
     * @return Flow emitting current user tier
     */
    operator fun invoke(): Flow<UserTier> = settingsRepository.getUserTier()
}
