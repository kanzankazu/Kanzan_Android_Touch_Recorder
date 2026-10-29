package com.kanzan.core.domain.usecase

import com.kanzan.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for checking accessibility service status
 */
class CheckAccessibilityUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    
    /**
     * Check if accessibility service is enabled
     * 
     * @return Flow emitting accessibility status
     */
    operator fun invoke(): Flow<Boolean> = settingsRepository.isAccessibilityEnabled()
}
