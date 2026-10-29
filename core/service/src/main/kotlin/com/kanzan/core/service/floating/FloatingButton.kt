package com.kanzan.core.service.floating

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.kanzan.core.service.ServiceState

/**
 * Draggable floating button (collapsed state)
 */
@Composable
fun FloatingButton(
    serviceState: ServiceState,
    position: Offset,
    onPositionChange: (Offset) -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExecuting = when (serviceState) {
        is ServiceState.Playing,
        is ServiceState.Recording,
        is ServiceState.AutoClicking -> true
        else -> false
    }
    
    val elevation by animateDpAsState(
        targetValue = if (isExecuting) 12.dp else 8.dp,
        label = "elevation"
    )
    
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(elevation = elevation, shape = CircleShape)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
            .pointerInput(Unit) {
                var isDragging = false
                var dragStartPosition = Offset.Zero
                
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = false
                        dragStartPosition = offset
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        isDragging = true
                        onPositionChange(position + dragAmount)
                    },
                    onDragEnd = {
                        // If barely moved, treat as tap
                        if (!isDragging) {
                            onTap()
                        }
                    },
                    onDragCancel = {
                        // Reset drag state
                        isDragging = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Open panel",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(32.dp)
        )
    }
}
