package com.kanzan.core.data.model

import com.kanzan.core.domain.model.Macro
import com.kanzan.core.domain.model.TouchEvent
import com.kanzan.core.domain.model.TouchEventType
import kotlinx.serialization.Serializable

/**
 * Data transfer object for Macro persistence
 */
@Serializable
data class MacroDto(
    val id: String,
    val name: String,
    val description: String = "",
    val events: List<TouchEventDto>,
    val createdAt: Long,
    val updatedAt: Long,
    val duration: Long,
    val isPremium: Boolean = false
)

@Serializable
data class TouchEventDto(
    val type: String,
    val x: Float,
    val y: Float,
    val timestamp: Long,
    val delay: Long = 0,
    val pointer: Int = 0,
    val pressure: Float = 1f
)

// Domain → DTO
fun Macro.toDto(): MacroDto = MacroDto(
    id = id,
    name = name,
    description = description,
    events = events.map { it.toDto() },
    createdAt = createdAt,
    updatedAt = updatedAt,
    duration = duration,
    isPremium = isPremium
)

fun TouchEvent.toDto(): TouchEventDto = TouchEventDto(
    type = type.name,
    x = x,
    y = y,
    timestamp = timestamp,
    delay = delay,
    pointer = pointer,
    pressure = pressure
)

// DTO → Domain
fun MacroDto.toDomain(): Macro = Macro(
    id = id,
    name = name,
    description = description,
    events = events.map { it.toDomain() },
    createdAt = createdAt,
    updatedAt = updatedAt,
    duration = duration,
    isPremium = isPremium
)

fun TouchEventDto.toDomain(): TouchEvent = TouchEvent(
    type = TouchEventType.valueOf(type),
    x = x,
    y = y,
    timestamp = timestamp,
    delay = delay,
    pointer = pointer,
    pressure = pressure
)
