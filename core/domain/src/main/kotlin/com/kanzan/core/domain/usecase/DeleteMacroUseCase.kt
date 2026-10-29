package com.kanzan.core.domain.usecase

import com.kanzan.core.domain.repository.MacroRepository
import javax.inject.Inject

/**
 * Use case for deleting a saved macro
 */
class DeleteMacroUseCase @Inject constructor(
    private val macroRepository: MacroRepository
) {
    
    /**
     * Delete a macro by ID
     * 
     * @param macroId Macro unique identifier
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(macroId: String): Result<Unit> {
        return macroRepository.deleteMacro(macroId)
    }
}
