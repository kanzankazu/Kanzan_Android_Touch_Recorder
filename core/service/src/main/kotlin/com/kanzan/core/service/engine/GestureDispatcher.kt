package com.kanzan.core.service.engine

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import com.kanzan.core.domain.model.TouchEvent
import com.kanzan.core.domain.model.TouchEventType
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Converts TouchEvent lists into Android GestureDescription and dispatches via AccessibilityService
 * 
 * Supports single-touch and multi-touch gestures with accurate timing
 */
class GestureDispatcher(
    private val service: AccessibilityService
) {
    
    /**
     * Dispatch a sequence of touch events as gestures
     * 
     * @param events List of touch events in chronological order
     * @return Result indicating success or failure
     */
    suspend fun dispatchGesture(events: List<TouchEvent>): Result<Unit> {
        if (events.isEmpty()) {
            return Result.failure(IllegalArgumentException("Events list cannot be empty"))
        }
        
        return try {
            // Group events by pointer for multi-touch support
            val pointerGroups = events.groupBy { it.pointer }
            
            // Build gesture description
            val gestureBuilder = GestureDescription.Builder()
            
            pointerGroups.forEach { (pointer, pointerEvents) ->
                val stroke = buildStrokeFromEvents(pointerEvents)
                if (stroke != null) {
                    gestureBuilder.addStroke(stroke)
                }
            }
            
            val gesture = gestureBuilder.build()
            
            // Dispatch with suspension until completion
            dispatchGestureWithCallback(gesture)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Build a StrokeDescription from a sequence of events for single pointer
     * 
     * @param events Events for single pointer, sorted by timestamp
     * @return StrokeDescription or null if invalid
     */
    private fun buildStrokeFromEvents(events: List<TouchEvent>): GestureDescription.StrokeDescription? {
        if (events.isEmpty()) return null
        
        // Separate into DOWN/MOVE/UP sequences
        val downEvent = events.firstOrNull { it.type == TouchEventType.DOWN } ?: return null
        val upEvent = events.lastOrNull { it.type == TouchEventType.UP }
        
        // Build path from coordinates
        val path = Path()
        path.moveTo(downEvent.x, downEvent.y)
        
        // Add intermediate MOVE events
        events.filter { it.type == TouchEventType.MOVE }.forEach { event ->
            path.lineTo(event.x, event.y)
        }
        
        // Add final UP position if exists
        upEvent?.let { path.lineTo(it.x, it.y) }
        
        // Calculate duration
        val startTime = downEvent.timestamp
        val endTime = upEvent?.timestamp ?: downEvent.timestamp
        val duration = endTime - startTime
        
        return GestureDescription.StrokeDescription(
            path,
            startTime,
            duration,
            false // willContinue = false (gesture completes)
        )
    }
    
    /**
     * Dispatch a simple tap gesture at coordinates
     * 
     * @param x Screen X coordinate
     * @param y Screen Y coordinate
     * @param duration Tap duration in ms (default 50ms)
     * @return Result indicating success or failure
     */
    suspend fun dispatchTap(x: Float, y: Float, duration: Long = 50): Result<Unit> {
        return try {
            val path = Path().apply {
                moveTo(x, y)
            }
            
            val stroke = GestureDescription.StrokeDescription(path, 0, duration)
            val gesture = GestureDescription.Builder()
                .addStroke(stroke)
                .build()
            
            dispatchGestureWithCallback(gesture)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Dispatch a swipe gesture from (x1, y1) to (x2, y2)
     * 
     * @param x1 Start X coordinate
     * @param y1 Start Y coordinate
     * @param x2 End X coordinate
     * @param y2 End Y coordinate
     * @param duration Swipe duration in ms (default 300ms)
     * @return Result indicating success or failure
     */
    suspend fun dispatchSwipe(
        x1: Float, y1: Float,
        x2: Float, y2: Float,
        duration: Long = 300
    ): Result<Unit> {
        return try {
            val path = Path().apply {
                moveTo(x1, y1)
                lineTo(x2, y2)
            }
            
            val stroke = GestureDescription.StrokeDescription(path, 0, duration)
            val gesture = GestureDescription.Builder()
                .addStroke(stroke)
                .build()
            
            dispatchGestureWithCallback(gesture)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Dispatch long press gesture
     * 
     * @param x Screen X coordinate
     * @param y Screen Y coordinate
     * @param duration Long press duration (default 1000ms)
     * @return Result indicating success or failure
     */
    suspend fun dispatchLongPress(
        x: Float,
        y: Float,
        duration: Long = 1000
    ): Result<Unit> {
        return dispatchTap(x, y, duration)
    }
    
    /**
     * Dispatch gesture with coroutine suspension until completion
     * 
     * @param gesture GestureDescription to dispatch
     * @throws Exception if dispatch fails
     */
    private suspend fun dispatchGestureWithCallback(gesture: GestureDescription) {
        suspendCancellableCoroutine { continuation ->
            val callback = object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    continuation.resume(Unit)
                }
                
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    continuation.resumeWithException(
                        Exception("Gesture dispatch was cancelled")
                    )
                }
            }
            
            val dispatched = service.dispatchGesture(gesture, callback, null)
            
            if (!dispatched) {
                continuation.resumeWithException(
                    Exception("Failed to dispatch gesture (service may not be ready)")
                )
            }
            
            continuation.invokeOnCancellation {
                // Cleanup if coroutine is cancelled
            }
        }
    }
    
    /**
     * Validate touch coordinates are within screen bounds
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if valid, false otherwise
     */
    fun validateCoordinates(x: Float, y: Float): Boolean {
        // Basic validation (positive coordinates)
        // More sophisticated validation requires display metrics
        return x >= 0 && y >= 0
    }
}
