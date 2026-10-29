package com.kanzan.core.service

/**
 * Represents the current state of KanzanAccessibilityService
 * 
 * Used for UI updates and state management
 */
sealed class ServiceState {
    
    /**
     * Service is idle, not executing any action
     */
    object Idle : ServiceState()
    
    /**
     * Service is recording touch events
     * 
     * @property eventCount Number of events recorded so far
     * @property duration Recording duration in milliseconds
     */
    data class Recording(
        val eventCount: Int,
        val duration: Long
    ) : ServiceState()
    
    /**
     * Service is playing back a macro
     * 
     * @property macroName Name of the macro being played
     * @property progress Playback progress (0.0 - 1.0)
     * @property currentEvent Index of current event being executed
     * @property totalEvents Total number of events in macro
     */
    data class Playing(
        val macroName: String,
        val progress: Float,
        val currentEvent: Int,
        val totalEvents: Int
    ) : ServiceState()
    
    /**
     * Service is executing auto-click
     * 
     * @property configName Name of the auto-click config
     * @property clickCount Number of clicks executed so far
     * @property targetCount Total clicks to execute (-1 = unlimited)
     */
    data class AutoClicking(
        val configName: String,
        val clickCount: Int,
        val targetCount: Int
    ) : ServiceState()
    
    /**
     * Service execution is paused
     * 
     * @property previousState State before pause (for resume)
     */
    data class Paused(
        val previousState: ServiceState
    ) : ServiceState()
    
    /**
     * Service encountered an error
     * 
     * @property message Error description
     * @property error Optional exception for debugging
     */
    data class Error(
        val message: String,
        val error: Throwable? = null
    ) : ServiceState()
}

/**
 * Helper extension to check if service is currently executing
 */
fun ServiceState.isExecuting(): Boolean = when (this) {
    is ServiceState.Playing,
    is ServiceState.AutoClicking,
    is ServiceState.Recording -> true
    else -> false
}

/**
 * Helper extension to get progress percentage (0-100)
 */
fun ServiceState.getProgressPercentage(): Int = when (this) {
    is ServiceState.Playing -> (progress * 100).toInt()
    is ServiceState.AutoClicking -> {
        if (targetCount <= 0) 0 else (clickCount * 100 / targetCount)
    }
    else -> 0
}
