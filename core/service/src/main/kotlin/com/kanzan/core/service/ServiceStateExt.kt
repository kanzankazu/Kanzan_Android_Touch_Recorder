package com.kanzan.core.service

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Maps ServiceState to corresponding Material icon
 * 
 * Visual representation of current service state in floating button
 * 
 * @return ImageVector icon for current state
 */
fun ServiceState.toIcon(): ImageVector = when (this) {
    is ServiceState.Idle -> Icons.Default.PlayArrow
    is ServiceState.Recording -> Icons.Default.FiberManualRecord
    is ServiceState.Playing -> Icons.Default.PlayArrow
    is ServiceState.AutoClicking -> Icons.Default.TouchApp
    is ServiceState.Paused -> Icons.Default.Pause
    is ServiceState.Error -> Icons.Default.Error
}

/**
 * Maps ServiceState to icon tint color
 * 
 * Provides visual feedback for critical states (Recording, Error)
 * 
 * @return Color for icon tint, or Color.Unspecified for theme default
 */
fun ServiceState.toIconColor(): Color = when (this) {
    is ServiceState.Recording -> Color.Red
    is ServiceState.Error -> Color.Red
    else -> Color.Unspecified // Use theme default
}

/**
 * Determines if icon should pulse/animate
 * 
 * Active operations (Recording, AutoClicking) get pulse animation
 * to indicate ongoing activity
 * 
 * @return true if icon should pulse, false for static
 */
fun ServiceState.shouldPulseIcon(): Boolean = when (this) {
    is ServiceState.Recording -> true
    is ServiceState.AutoClicking -> true
    else -> false
}
