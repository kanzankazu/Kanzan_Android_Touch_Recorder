package com.kanzan.core.data.repository

import android.content.Context
import com.kanzan.core.data.model.MacroDto
import com.kanzan.core.data.model.toDomain
import com.kanzan.core.data.model.toDto
import com.kanzan.core.data.util.FileOperations
import com.kanzan.core.domain.model.Macro
import com.kanzan.core.domain.repository.MacroRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for macro persistence using JSON files
 */
@Singleton
class MacroRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MacroRepository {
    
    private val macrosDir = File(context.filesDir, "macros")
    private val _macrosFlow = MutableStateFlow<List<Macro>>(emptyList())
    
    init {
        macrosDir.mkdirs()
        refreshMacroList()
    }
    
    override fun getAllMacros(): Flow<List<Macro>> = _macrosFlow
    
    override suspend fun getMacroById(id: String): Macro? {
        val file = File(macrosDir, "$id.json")
        return FileOperations.readJson(file, MacroDto.serializer())
            .getOrNull()
            ?.toDomain()
    }
    
    override suspend fun saveMacro(macro: Macro): Result<Unit> {
        val file = File(macrosDir, "${macro.id}.json")
        val dto = macro.toDto()
        
        return FileOperations.writeJson(file, dto, MacroDto.serializer())
            .also { result ->
                if (result.isSuccess) {
                    refreshMacroList()
                }
            }
    }
    
    override suspend fun deleteMacro(id: String): Result<Unit> {
        val file = File(macrosDir, "$id.json")
        return FileOperations.deleteFile(file)
            .also { result ->
                if (result.isSuccess) {
                    refreshMacroList()
                }
            }
    }
    
    override suspend fun updateMacro(macro: Macro): Result<Unit> {
        return saveMacro(macro)
    }
    
    override suspend fun getMacroCount(): Int {
        return FileOperations.listJsonFiles(macrosDir).size
    }
    
    /**
     * Refresh macro list from disk and update flow
     */
    private fun refreshMacroList() {
        val files = macrosDir.listFiles { file ->
            file.isFile && file.extension == "json"
        } ?: emptyArray()
        
        val macros = files.mapNotNull { file ->
            try {
                val jsonString = file.readText()
                val dto = Json.decodeFromString(MacroDto.serializer(), jsonString)
                dto.toDomain()
            } catch (e: Exception) {
                null
            }
        }.sortedByDescending { it.updatedAt }
        
        _macrosFlow.value = macros
    }
}
