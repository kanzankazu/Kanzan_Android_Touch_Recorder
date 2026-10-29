package com.kanzan.core.domain.usecase

import com.kanzan.core.domain.model.Macro
import com.kanzan.core.domain.repository.MacroRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all saved macros
 */
class GetAllMacrosUseCase @Inject constructor(
    private val macroRepository: MacroRepository
) {
    
    /**
     * Get all macros as reactive flow
     * 
     * @return Flow emitting list of all macros
     */
    operator fun invoke(): Flow<List<Macro>> = macroRepository.getAllMacros()
}
