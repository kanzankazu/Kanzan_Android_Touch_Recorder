package com.kanzan.core.service.floating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kanzan.core.service.ServiceState

/**
 * Floating control panel (expanded state)
 */
@Composable
fun FloatingPanel(
    serviceState: ServiceState,
    onPlayClick: () -> Unit,
    onRecordClick: () -> Unit,
    onStopClick: () -> Unit,
    onCloseClick: () -> Unit,
    onHideClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRecording = serviceState is ServiceState.Recording
    val isExecuting = when (serviceState) {
        is ServiceState.Playing,
        is ServiceState.Recording,
        is ServiceState.AutoClicking -> true
        else -> false
    }
    
    Surface(
        modifier = modifier
            .width(280.dp)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kanzan Controls",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onPlayClick,
                    enabled = serviceState is ServiceState.Idle,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Play")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onRecordClick,
                    enabled = serviceState is ServiceState.Idle || serviceState is ServiceState.Recording,
                    modifier = Modifier.weight(1f),
                    colors = if (isRecording) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    } else {
                        ButtonDefaults.buttonColors()
                    }
                ) {
                    Text(if (isRecording) "Stop" else "Record")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onStopClick,
                    enabled = isExecuting,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Stop")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress indicator
            when (serviceState) {
                is ServiceState.Recording -> {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Recording: ${serviceState.eventCount} events, ${serviceState.duration / 1000}s",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is ServiceState.Playing -> {
                    LinearProgressIndicator(
                        progress = serviceState.progress,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${serviceState.currentEvent}/${serviceState.totalEvents} events",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                is ServiceState.AutoClicking -> {
                    val progress = if (serviceState.targetCount > 0) {
                        serviceState.clickCount.toFloat() / serviceState.targetCount
                    } else 0f
                    
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${serviceState.clickCount} clicks",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                is ServiceState.Error -> {
                    Text(
                        text = "Error: ${serviceState.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    Text(
                        text = "Ready",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Hide button
            OutlinedButton(
                onClick = onHideClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hide Floating Controls")
            }
        }
    }
}
