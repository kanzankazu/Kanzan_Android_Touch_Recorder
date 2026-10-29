package com.kanzan.core.domain.repository

import com.kanzan.core.domain.model.AutoClickConfig
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for auto-click configuration operations
 */
interface AutoClickRepository {
    
    /**
     * Get all saved auto-click configs as reactive flow
     * @return Flow emitting list of all configs
     */
    fun getAllConfigs(): Flow<List<AutoClickConfig>>
    
    /**
     * Get a specific config by ID
     * @param id Config unique identifier
     * @return Config if found, null otherwise
     */
    suspend fun getConfigById(id: String): AutoClickConfig?
    
    /**
     * Save a new config
     * @param config Config to save
     * @return Result indicating success or failure
     */
    suspend fun saveConfig(config: AutoClickConfig): Result<Unit>
    
    /**
     * Delete a config by ID
     * @param id Config unique identifier
     * @return Result indicating success or failure
     */
    suspend fun deleteConfig(id: String): Result<Unit>
    
    /**
     * Update an existing config
     * @param config Updated config
     * @return Result indicating success or failure
     */
    suspend fun updateConfig(config: AutoClickConfig): Result<Unit>
    
    /**
     * Get currently active config
     * @return Active config if any, null otherwise
     */
    suspend fun getActiveConfig(): AutoClickConfig?
}
