package com.kanzan.core.service.engine

import kotlinx.coroutines.delay

/**
 * Timing engine for accurate gesture playback
 * 
 * Handles delay calculations and compensates for dispatch overhead
 * to maintain timing accuracy within ±50ms target
 */
class TimingEngine {
    
    private var executionStartTime: Long = 0
    private var lastDispatchTime: Long = 0
    private var accumulatedDrift: Long = 0
    
    /**
     * Reset timing engine for new execution
     */
    fun reset() {
        executionStartTime = System.currentTimeMillis()
        lastDispatchTime = executionStartTime
        accumulatedDrift = 0
    }
    
    /**
     * Calculate delay needed to maintain timing accuracy
     * 
     * @param targetTimestamp When event should occur (from TouchEvent.timestamp)
     * @return Delay in milliseconds before next dispatch
     */
    fun calculateDelay(targetTimestamp: Long): Long {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - executionStartTime
        
        // Target time when event should occur
        val targetTime = executionStartTime + targetTimestamp
        
        // Calculate delay needed
        val delay = targetTime - currentTime
        
        // Track drift for monitoring
        accumulatedDrift = elapsedTime - targetTimestamp
        
        // Never return negative delay
        return maxOf(0, delay)
    }
    
    /**
     * Execute delay with timing compensation
     * 
     * @param targetTimestamp When event should occur
     */
    suspend fun waitUntil(targetTimestamp: Long) {
        val delayMs = calculateDelay(targetTimestamp)
        if (delayMs > 0) {
            delay(delayMs)
        }
    }
    
    /**
     * Get current timing drift in milliseconds
     * 
     * Positive = behind schedule, Negative = ahead of schedule
     */
    fun getCurrentDrift(): Long = accumulatedDrift
    
    /**
     * Check if drift exceeds acceptable threshold
     * 
     * @param threshold Maximum acceptable drift in ms (default 50ms)
     * @return true if drift exceeds threshold
     */
    fun isDriftExcessive(threshold: Long = 50): Boolean {
        return kotlin.math.abs(accumulatedDrift) > threshold
    }
    
    /**
     * Mark that a gesture was dispatched
     * 
     * Used for tracking dispatch overhead
     */
    fun markDispatch() {
        lastDispatchTime = System.currentTimeMillis()
    }
    
    /**
     * Calculate time since last dispatch
     * 
     * @return Milliseconds since last dispatch
     */
    fun timeSinceLastDispatch(): Long {
        return System.currentTimeMillis() - lastDispatchTime
    }
    
    /**
     * Measure average dispatch latency over multiple samples
     * 
     * @param samples Number of test dispatches to perform
     * @return Average latency in milliseconds
     */
    suspend fun measureDispatchLatency(samples: Int = 10): Long {
        val latencies = mutableListOf<Long>()
        
        repeat(samples) {
            val startTime = System.currentTimeMillis()
            delay(1) // Simulate minimal dispatch
            val endTime = System.currentTimeMillis()
            latencies.add(endTime - startTime)
        }
        
        return latencies.average().toLong()
    }
}
