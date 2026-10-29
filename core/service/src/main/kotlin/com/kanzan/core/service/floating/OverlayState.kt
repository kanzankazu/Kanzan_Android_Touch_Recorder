package com.kanzan.core.service.floating

import androidx.compose.ui.geometry.Offset
import com.kanzan.core.service.ServiceState

/**
 * State for floating overlay UI
 */
data class OverlayState(
    val isExpanded: Boolean = false,
    val position: Offset = Offset(100f, 300f),
    val serviceState: ServiceState = ServiceState.Idle
)
