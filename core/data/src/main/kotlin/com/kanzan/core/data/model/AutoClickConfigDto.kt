package com.kanzan.core.data.model

import com.kanzan.core.domain.model.AutoClickConfig
import com.kanzan.core.domain.model.ClickTarget
import kotlinx.serialization.Serializable

/**
 * Data transfer object for AutoClickConfig persistence
 */
@Serializable
data class AutoClickConfigDto(
    val id: String,
    val name: String,
    val targets: List<ClickTargetDto>,
    val interval: Long,
    val repeatCount: Int = -1,
    val isEnabled: Boolean = false,
    val isPremium: Boolean = false
)

@Serializable
data class ClickTargetDto(
    val x: Float,
    val y: Float,
    val order: Int,
    val delayBefore: Long = 0
)

// Domain → DTO
fun AutoClickConfig.toDto(): AutoClickConfigDto = AutoClickConfigDto(
    id = id,
    name = name,
    targets = targets.map { it.toDto() },
    interval = interval,
    repeatCount = repeatCount,
    isEnabled = isEnabled,
    isPremium = isPremium
)

fun ClickTarget.toDto(): ClickTargetDto = ClickTargetDto(
    x = x,
    y = y,
    order = order,
    delayBefore = delayBefore
)

// DTO → Domain
fun AutoClickConfigDto.toDomain(): AutoClickConfig = AutoClickConfig(
    id = id,
    name = name,
    targets = targets.map { it.toDomain() },
    interval = interval,
    repeatCount = repeatCount,
    isEnabled = isEnabled,
    isPremium = isPremium
)

fun ClickTargetDto.toDomain(): ClickTarget = ClickTarget(
    x = x,
    y = y,
    order = order,
    delayBefore = delayBefore
)
