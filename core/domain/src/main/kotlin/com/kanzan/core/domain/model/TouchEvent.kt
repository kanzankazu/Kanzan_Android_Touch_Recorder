package com.kanzan.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a single touch event within a macro
 * 
 * @property type Type of touch event (DOWN, MOVE, UP, etc)
 * @property x Screen X coordinate
 * @property y Screen Y coordinate
 * @property timestamp Relative time from recording start (ms)
 * @property delay Delay before this event (ms)
 * @property pointer Multi-touch pointer index (0 = first finger, 1 = second, etc)
 * @property pressure Touch pressure (0.0-1.0)
 */
@Serializable
data class TouchEvent(
    val type: TouchEventType,
    val x: Float,
    val y: Float,
    val timestamp: Long,
    val delay: Long = 0,
    val pointer: Int = 0,
    val pressure: Float = 1f
)

/**
 * Touch event types supported by the recorder
 */
@Serializable
enum class TouchEventType {
    /** Finger down (start of touch) */
    DOWN,
    
    /** Finger move while touching */
    MOVE,
    
    /** Finger up (end of touch) */
    UP,
    
    /** Long press detected */
    LONG_PRESS,
    
    /** Double tap detected */
    DOUBLE_TAP
}
