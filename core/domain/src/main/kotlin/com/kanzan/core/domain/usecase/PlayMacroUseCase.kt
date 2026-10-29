package com.kanzan.core.domain.usecase

import com.kanzan.core.domain.model.Macro
import com.kanzan.core.domain.repository.MacroRepository
import javax.inject.Inject

/**
 * Use case for retrieving a macro for playback
 * 
 * Note: Actual playback execution is handled by the Service layer
 */
class PlayMacroUseCase @Inject constructor(
    private val macroRepository: MacroRepository
) {
    
    /**
     * Get a macro by ID for playback
     * 
     * @param macroId Macro unique identifier
     * @return Result with macro or error if not found
     */
    suspend operator fun invoke(macroId: String): Result<Macro> {
        val macro = macroRepository.getMacroById(macroId)
            ?: return Result.failure(Exception("Macro not found"))
        
        return Result.success(macro)
    }
}
