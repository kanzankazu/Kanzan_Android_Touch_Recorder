package com.kanzan.core.domain.usecase

import com.kanzan.core.domain.model.FreeTierLimits
import com.kanzan.core.domain.model.Macro
import com.kanzan.core.domain.model.TouchEvent
import com.kanzan.core.domain.model.UserTier
import com.kanzan.core.domain.repository.MacroRepository
import com.kanzan.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for recording and saving a new macro
 * 
 * Validates free tier recording duration limits before saving
 */
class RecordMacroUseCase @Inject constructor(
    private val macroRepository: MacroRepository,
    private val settingsRepository: SettingsRepository
) {
    
    /**
     * Record a new macro with the given events
     * 
     * @param events List of recorded touch events
     * @param name User-defined macro name
     * @param description Optional description
     * @return Result with saved macro or error
     */
    suspend operator fun invoke(
        events: List<TouchEvent>,
        name: String,
        description: String = ""
    ): Result<Macro> {
        val tier = settingsRepository.getUserTier().first()
        val duration = events.lastOrNull()?.timestamp ?: 0
        
        // Free tier validation
        if (tier == UserTier.FREE && duration > FreeTierLimits().maxRecordingDuration) {
            return Result.failure(
                Exception("Recording exceeds free tier limit of ${FreeTierLimits().maxRecordingDuration / 1000}s")
            )
        }
        
        // Check macro count limit for free tier
        if (tier == UserTier.FREE) {
            val count = macroRepository.getMacroCount()
            if (count >= FreeTierLimits().maxMacroStorage) {
                return Result.failure(
                    Exception("Free tier allows maximum ${FreeTierLimits().maxMacroStorage} macros")
                )
            }
        }
        
        val currentTime = System.currentTimeMillis()
        val macro = Macro(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            events = events,
            createdAt = currentTime,
            updatedAt = currentTime,
            duration = duration
        )
        
        return macroRepository.saveMacro(macro).map { macro }
    }
}
