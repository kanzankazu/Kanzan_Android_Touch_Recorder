package com.kanzan.core.domain.repository

import com.kanzan.core.domain.model.Macro
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for macro data operations
 */
interface MacroRepository {
    
    /**
     * Get all saved macros as reactive flow
     * @return Flow emitting list of all macros
     */
    fun getAllMacros(): Flow<List<Macro>>
    
    /**
     * Get a specific macro by ID
     * @param id Macro unique identifier
     * @return Macro if found, null otherwise
     */
    suspend fun getMacroById(id: String): Macro?
    
    /**
     * Save a new macro
     * @param macro Macro to save
     * @return Result indicating success or failure
     */
    suspend fun saveMacro(macro: Macro): Result<Unit>
    
    /**
     * Delete a macro by ID
     * @param id Macro unique identifier
     * @return Result indicating success or failure
     */
    suspend fun deleteMacro(id: String): Result<Unit>
    
    /**
     * Update an existing macro
     * @param macro Updated macro
     * @return Result indicating success or failure
     */
    suspend fun updateMacro(macro: Macro): Result<Unit>
    
    /**
     * Get total count of saved macros
     * @return Number of macros
     */
    suspend fun getMacroCount(): Int
}
