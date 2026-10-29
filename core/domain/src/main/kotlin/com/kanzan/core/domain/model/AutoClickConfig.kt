package com.kanzan.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Configuration for auto-click feature
 * 
 * @property id Unique identifier (UUID)
 * @property name User-defined configuration name
 * @property targets List of click targets (single or multi-target)
 * @property interval Interval between clicks in milliseconds
 * @property repeatCount Number of repetitions (-1 = unlimited, premium only)
 * @property isEnabled Whether this config is currently active
 * @property isPremium Whether this is a premium-only config
 */
@Serializable
data class AutoClickConfig(
    val id: String,
    val name: String,
    val targets: List<ClickTarget>,
    val interval: Long,
    val repeatCount: Int = -1,
    val isEnabled: Boolean = false,
    val isPremium: Boolean = false
)

/**
 * Single click target within an auto-click configuration
 * 
 * @property x Screen X coordinate
 * @property y Screen Y coordinate
 * @property order Sequence order for multi-target clicking
 * @property delayBefore Custom delay before this target (ms)
 */
@Serializable
data class ClickTarget(
    val x: Float,
    val y: Float,
    val order: Int,
    val delayBefore: Long = 0
)
