package com.kanzan.core.service.recording

import android.view.MotionEvent
import com.kanzan.core.domain.model.TouchEvent
import com.kanzan.core.domain.model.TouchEventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Core touch event recorder
 * 
 * Captures touch events with accurate timing and converts them to TouchEvent entities
 */
class TouchEventRecorder {
    
    private val recordedEvents = ConcurrentLinkedQueue<TouchEvent>()
    private var recordingStartTime = 0L
    private var isRecording = false
    
    private val _eventCount = MutableStateFlow(0)
    val eventCount: StateFlow<Int> = _eventCount
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration
    
    /**
     * Start recording touch events
     */
    fun startRecording() {
        if (isRecording) return
        
        isRecording = true
        recordingStartTime = System.currentTimeMillis()
        recordedEvents.clear()
        _eventCount.value = 0
        _duration.value = 0
    }
    
    /**
     * Stop recording and return captured events
     * 
     * @return List of recorded touch events in chronological order
     */
    fun stopRecording(): List<TouchEvent> {
        if (!isRecording) return emptyList()
        
        isRecording = false
        return recordedEvents.toList()
    }
    
    /**
     * Record a touch event from MotionEvent
     * 
     * @param event Android MotionEvent from accessibility service
     */
    fun recordEvent(event: MotionEvent) {
        if (!isRecording) return
        
        val currentTime = System.currentTimeMillis()
        val timestamp = currentTime - recordingStartTime
        
        // Update duration
        _duration.value = timestamp
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                recordTouchEvent(
                    type = TouchEventType.DOWN,
                    x = event.x,
                    y = event.y,
                    timestamp = timestamp,
                    pointer = 0,
                    pressure = event.pressure
                )
            }
            
            MotionEvent.ACTION_MOVE -> {
                // Record all pointer moves
                for (i in 0 until event.pointerCount) {
                    recordTouchEvent(
                        type = TouchEventType.MOVE,
                        x = event.getX(i),
                        y = event.getY(i),
                        timestamp = timestamp,
                        pointer = i,
                        pressure = event.getPressure(i)
                    )
                }
            }
            
            MotionEvent.ACTION_UP -> {
                recordTouchEvent(
                    type = TouchEventType.UP,
                    x = event.x,
                    y = event.y,
                    timestamp = timestamp,
                    pointer = 0,
                    pressure = event.pressure
                )
            }
            
            MotionEvent.ACTION_POINTER_DOWN -> {
                val pointerIndex = event.actionIndex
                recordTouchEvent(
                    type = TouchEventType.DOWN,
                    x = event.getX(pointerIndex),
                    y = event.getY(pointerIndex),
                    timestamp = timestamp,
                    pointer = pointerIndex,
                    pressure = event.getPressure(pointerIndex)
                )
            }
            
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                recordTouchEvent(
                    type = TouchEventType.UP,
                    x = event.getX(pointerIndex),
                    y = event.getY(pointerIndex),
                    timestamp = timestamp,
                    pointer = pointerIndex,
                    pressure = event.getPressure(pointerIndex)
                )
            }
        }
    }
    
    /**
     * Record a touch event with calculated delay
     */
    private fun recordTouchEvent(
        type: TouchEventType,
        x: Float,
        y: Float,
        timestamp: Long,
        pointer: Int,
        pressure: Float
    ) {
        val previousEvent = recordedEvents.lastOrNull()
        val delay = if (previousEvent != null) {
            timestamp - previousEvent.timestamp
        } else {
            0L
        }
        
        val touchEvent = TouchEvent(
            type = type,
            x = x,
            y = y,
            timestamp = timestamp,
            delay = delay,
            pointer = pointer,
            pressure = pressure
        )
        
        recordedEvents.add(touchEvent)
        _eventCount.value = recordedEvents.size
    }
    
    /**
     * Check if currently recording
     */
    fun isRecording(): Boolean = isRecording
    
    /**
     * Get current event count
     */
    fun getEventCount(): Int = recordedEvents.size
    
    /**
     * Get current recording duration in milliseconds
     */
    fun getDuration(): Long {
        return if (isRecording) {
            System.currentTimeMillis() - recordingStartTime
        } else {
            _duration.value
        }
    }
    
    /**
     * Cancel recording and discard events
     */
    fun cancelRecording() {
        isRecording = false
        recordedEvents.clear()
        _eventCount.value = 0
        _duration.value = 0
    }
}
