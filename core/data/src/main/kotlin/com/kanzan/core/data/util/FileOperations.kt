package com.kanzan.core.data.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Thread-safe file operations utility for JSON persistence
 */
object FileOperations {
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    /**
     * Write data as JSON to file
     * 
     * @param file Target file
     * @param data Data to serialize
     * @param serializer Kotlin serialization serializer
     * @return Result indicating success or failure
     */
    suspend fun <T> writeJson(
        file: File,
        data: T,
        serializer: KSerializer<T>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Ensure parent directory exists
            file.parentFile?.mkdirs()
            
            // Serialize to JSON string
            val jsonString = json.encodeToString(serializer, data)
            
            // Write to file atomically (write to temp, then rename)
            val tempFile = File(file.parentFile, "${file.name}.tmp")
            tempFile.writeText(jsonString)
            tempFile.renameTo(file)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Read and deserialize JSON from file
     * 
     * @param file Source file
     * @param serializer Kotlin serialization serializer
     * @return Result with deserialized data or error
     */
    suspend fun <T> readJson(
        file: File,
        serializer: KSerializer<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            if (!file.exists()) {
                return@withContext Result.failure(
                    IllegalArgumentException("File does not exist: ${file.path}")
                )
            }
            
            val jsonString = file.readText()
            val data = json.decodeFromString(serializer, jsonString)
            
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * List all JSON files in directory
     * 
     * @param dir Directory to scan
     * @return List of JSON files (excluding .tmp files)
     */
    suspend fun listJsonFiles(dir: File): List<File> = withContext(Dispatchers.IO) {
        if (!dir.exists() || !dir.isDirectory) {
            return@withContext emptyList()
        }
        
        dir.listFiles { file ->
            file.isFile && file.extension == "json" && !file.name.endsWith(".tmp")
        }?.toList() ?: emptyList()
    }
    
    /**
     * Delete file safely
     * 
     * @param file File to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteFile(file: File): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (file.exists()) {
                file.delete()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if file exists
     */
    fun exists(file: File): Boolean = file.exists()
}
