package com.kanzan.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a recorded macro containing sequence of touch events
 * 
 * @property id Unique identifier (UUID)
 * @property name User-defined macro name
 * @property description Optional description
 * @property events List of touch events in this macro
 * @property createdAt Creation timestamp (Unix epoch ms)
 * @property updatedAt Last update timestamp (Unix epoch ms)
 * @property duration Total macro duration in milliseconds
 * @property isPremium Whether this is a premium-only macro (post-MVP)
 */
@Serializable
data class Macro(
    val id: String,
    val name: String,
    val description: String = "",
    val events: List<TouchEvent>,
    val createdAt: Long,
    val updatedAt: Long,
    val duration: Long,
    val isPremium: Boolean = false
)
