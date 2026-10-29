package com.kanzan.core.service.engine

import com.kanzan.core.domain.model.AutoClickConfig
import com.kanzan.core.domain.model.ClickTarget
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

/**
 * Executes auto-click configurations with interval and repeat count control
 * 
 * Supports single-target and multi-target sequential clicking
 */
class AutoClickExecutor(
    private val gestureDispatcher: GestureDispatcher
) {
    
    private val _clickCount = MutableStateFlow(0)
    val clickCount: StateFlow<Int> = _clickCount
    
    private var isExecuting = false
    
    /**
     * Execute auto-click configuration
     * 
     * @param config Auto-click configuration
     * @param onProgress Progress callback (clickCount, targetCount)
     * @return Result indicating completion or error
     */
    suspend fun execute(
        config: AutoClickConfig,
        onProgress: (Int, Int) -> Unit = { _, _ -> }
    ): Result<Unit> {
        if (isExecuting) {
            return Result.failure(IllegalStateException("Already executing"))
        }
        
        isExecuting = true
        _clickCount.value = 0
        
        return try {
            // Sort targets by order for sequential execution
            val sortedTargets = config.targets.sortedBy { it.order }
            
            if (sortedTargets.isEmpty()) {
                return Result.failure(IllegalArgumentException("Config has no targets"))
            }
            
            val isUnlimited = config.repeatCount == -1
            val totalClicks = if (isUnlimited) Int.MAX_VALUE else config.repeatCount
            
            var currentClick = 0
            
            while (currentClick < totalClicks && coroutineContext.isActive) {
                // Execute one cycle through all targets
                for (target in sortedTargets) {
                    if (!coroutineContext.isActive) break
                    
                    // Apply target-specific delay if configured
                    if (target.delayBefore > 0) {
                        delay(target.delayBefore)
                    }
                    
                    // Dispatch tap gesture
                    val result = gestureDispatcher.dispatchTap(target.x, target.y)
                    
                    if (result.isFailure) {
                        isExecuting = false
                        return Result.failure(
                            result.exceptionOrNull() ?: Exception("Tap dispatch failed")
                        )
                    }
                    
                    currentClick++
                    _clickCount.value = currentClick
                    onProgress(currentClick, totalClicks)
                    
                    // Check if we've reached repeat count
                    if (currentClick >= totalClicks) break
                    
                    // Wait for interval before next click
                    if (config.interval > 0) {
                        delay(config.interval)
                    }
                }
            }
            
            isExecuting = false
            Result.success(Unit)
            
        } catch (e: Exception) {
            isExecuting = false
            Result.failure(e)
        }
    }
    
    /**
     * Execute single-target clicking (convenience method)
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @param interval Interval between clicks in ms
     * @param count Number of clicks (-1 for unlimited)
     * @param onProgress Progress callback
     * @return Result indicating completion or error
     */
    suspend fun executeSingleTarget(
        x: Float,
        y: Float,
        interval: Long,
        count: Int,
        onProgress: (Int, Int) -> Unit = { _, _ -> }
    ): Result<Unit> {
        val config = AutoClickConfig(
            id = "temp",
            name = "Single Target",
            targets = listOf(ClickTarget(x, y, order = 0)),
            interval = interval,
            repeatCount = count
        )
        
        return execute(config, onProgress)
    }
    
    /**
     * Execute multi-target sequential clicking
     * 
     * @param targets List of click targets
     * @param interval Interval between clicks in ms
     * @param cycles Number of cycles through all targets
     * @param onProgress Progress callback
     * @return Result indicating completion or error
     */
    suspend fun executeMultiTarget(
        targets: List<ClickTarget>,
        interval: Long,
        cycles: Int,
        onProgress: (Int, Int) -> Unit = { _, _ -> }
    ): Result<Unit> {
        val totalClicks = targets.size * cycles
        
        val config = AutoClickConfig(
            id = "temp",
            name = "Multi Target",
            targets = targets,
            interval = interval,
            repeatCount = totalClicks
        )
        
        return execute(config, onProgress)
    }
    
    /**
     * Stop current execution
     */
    fun stop() {
        isExecuting = false
    }
    
    /**
     * Check if currently executing
     */
    fun isRunning(): Boolean = isExecuting
    
    /**
     * Reset click counter
     */
    fun reset() {
        _clickCount.value = 0
    }
}
