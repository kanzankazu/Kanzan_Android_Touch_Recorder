package com.kanzan.core.data.repository

import android.content.Context
import com.kanzan.core.data.model.AutoClickConfigDto
import com.kanzan.core.data.model.toDomain
import com.kanzan.core.data.model.toDto
import com.kanzan.core.data.util.FileOperations
import com.kanzan.core.domain.model.AutoClickConfig
import com.kanzan.core.domain.repository.AutoClickRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for auto-click config persistence using JSON files
 */
@Singleton
class AutoClickRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AutoClickRepository {
    
    private val configsDir = File(context.filesDir, "autoclick")
    private val _configsFlow = MutableStateFlow<List<AutoClickConfig>>(emptyList())
    
    init {
        configsDir.mkdirs()
        refreshConfigList()
    }
    
    override fun getAllConfigs(): Flow<List<AutoClickConfig>> = _configsFlow
    
    override suspend fun getConfigById(id: String): AutoClickConfig? {
        val file = File(configsDir, "$id.json")
        return FileOperations.readJson(file, AutoClickConfigDto.serializer())
            .getOrNull()
            ?.toDomain()
    }
    
    override suspend fun saveConfig(config: AutoClickConfig): Result<Unit> {
        val file = File(configsDir, "${config.id}.json")
        val dto = config.toDto()
        
        return FileOperations.writeJson(file, dto, AutoClickConfigDto.serializer())
            .also { result ->
                if (result.isSuccess) {
                    refreshConfigList()
                }
            }
    }
    
    override suspend fun deleteConfig(id: String): Result<Unit> {
        val file = File(configsDir, "$id.json")
        return FileOperations.deleteFile(file)
            .also { result ->
                if (result.isSuccess) {
                    refreshConfigList()
                }
            }
    }
    
    override suspend fun updateConfig(config: AutoClickConfig): Result<Unit> {
        return saveConfig(config)
    }
    
    override suspend fun getActiveConfig(): AutoClickConfig? {
        return _configsFlow.value.firstOrNull { it.isEnabled }
    }
    
    /**
     * Refresh config list from disk and update flow
     */
    private fun refreshConfigList() {
        val files = configsDir.listFiles { file ->
            file.isFile && file.extension == "json"
        } ?: emptyArray()
        
        val configs = files.mapNotNull { file ->
            try {
                val jsonString = file.readText()
                val dto = Json.decodeFromString(AutoClickConfigDto.serializer(), jsonString)
                dto.toDomain()
            } catch (e: Exception) {
                null
            }
        }
        
        _configsFlow.value = configs
    }
}
