package com.kanzan.core.domain.usecase

import com.kanzan.core.domain.model.AutoClickConfig
import com.kanzan.core.domain.repository.AutoClickRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all auto-click configs
 */
class GetAllAutoClickConfigsUseCase @Inject constructor(
    private val autoClickRepository: AutoClickRepository
) {
    
    /**
     * Get all auto-click configs as reactive flow
     * 
     * @return Flow emitting list of all configs
     */
    operator fun invoke(): Flow<List<AutoClickConfig>> {
        return autoClickRepository.getAllConfigs()
    }
}
