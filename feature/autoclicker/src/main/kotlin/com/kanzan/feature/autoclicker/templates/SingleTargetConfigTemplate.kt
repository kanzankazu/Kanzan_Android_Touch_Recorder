package com.kanzan.feature.autoclicker.templates

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzan.core.designsystem.theme.KanzanTheme
import kotlin.math.roundToInt

/**
 * Template: Configuration for single target auto-click
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleTargetConfigTemplate(
    target: Offset,
    interval: Long,
    count: Int,
    onIntervalChange: (Long) -> Unit = {},
    onCountChange: (Int) -> Unit = {},
    onStartClick: () -> Unit = {},
    onStopClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    isRunning: Boolean = false,
    clickCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Auto Click Configuration") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isRunning) {
                        OutlinedButton(
                            onClick = onCancelClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = onStartClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Start")
                        }
                    } else {
                        Button(
                            onClick = onStopClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Stop")
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Target info
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Target Location",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "X: ${target.x.toInt()}  Y: ${target.y.toInt()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Interval slider
            if (!isRunning) {
                Column {
                    Text(
                        text = "Interval: ${interval}ms",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = interval.toFloat(),
                        onValueChange = { onIntervalChange(it.roundToInt().toLong()) },
                        valueRange = 100f..5000f,
                        steps = 48
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("100ms", style = MaterialTheme.typography.labelSmall)
                        Text("5000ms", style = MaterialTheme.typography.labelSmall)
                    }
                }
                
                // Count slider
                Column {
                    Text(
                        text = "Click Count: $count",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = count.toFloat(),
                        onValueChange = { onCountChange(it.roundToInt()) },
                        valueRange = 1f..100f,
                        steps = 98
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1", style = MaterialTheme.typography.labelSmall)
                        Text("100", style = MaterialTheme.typography.labelSmall)
                    }
                }
            } else {
                // Progress display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Clicking...",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = clickCount.toFloat() / count,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$clickCount / $count clicks",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

// Previews
@Preview(name = "Single Target Config", showBackground = true)
@Composable
private fun SingleTargetConfigTemplatePreview() {
    KanzanTheme {
        SingleTargetConfigTemplate(
            target = Offset(500f, 1000f),
            interval = 1000L,
            count = 10
        )
    }
}

@Preview(name = "Single Target Running", showBackground = true)
@Composable
private fun SingleTargetConfigRunningPreview() {
    KanzanTheme {
        SingleTargetConfigTemplate(
            target = Offset(500f, 1000f),
            interval = 1000L,
            count = 10,
            isRunning = true,
            clickCount = 6
        )
    }
}
