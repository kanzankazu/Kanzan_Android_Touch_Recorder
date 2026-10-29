package com.kanzan.feature.autoclicker.templates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzan.core.designsystem.theme.KanzanTheme
import kotlin.math.roundToInt

/**
 * Template: Configuration for multi-target auto-click
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiTargetConfigTemplate(
    targets: List<Offset>,
    interval: Long,
    cycles: Int,
    onIntervalChange: (Long) -> Unit = {},
    onCyclesChange: (Int) -> Unit = {},
    onStartClick: () -> Unit = {},
    onStopClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    isRunning: Boolean = false,
    clickCount: Int = 0,
    totalClicks: Int = 0,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Multi Target Configuration") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Target list
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Targets (${targets.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 150.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(targets) { index, target ->
                            Text(
                                text = "${index + 1}. X: ${target.x.toInt()}  Y: ${target.y.toInt()}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            // Configuration
            if (!isRunning) {
                // Interval slider
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
                
                // Cycles slider
                Column {
                    Text(
                        text = "Cycles: $cycles",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Total: ${targets.size * cycles} clicks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = cycles.toFloat(),
                        onValueChange = { onCyclesChange(it.roundToInt()) },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1", style = MaterialTheme.typography.labelSmall)
                        Text("10", style = MaterialTheme.typography.labelSmall)
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
                            progress = if (totalClicks > 0) clickCount.toFloat() / totalClicks else 0f,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$clickCount / $totalClicks clicks",
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
@Preview(name = "Multi Target Config", showBackground = true)
@Composable
private fun MultiTargetConfigTemplatePreview() {
    KanzanTheme {
        MultiTargetConfigTemplate(
            targets = listOf(
                Offset(300f, 500f),
                Offset(700f, 1000f),
                Offset(500f, 750f)
            ),
            interval = 1000L,
            cycles = 2
        )
    }
}

@Preview(name = "Multi Target Running", showBackground = true)
@Composable
private fun MultiTargetConfigRunningPreview() {
    KanzanTheme {
        MultiTargetConfigTemplate(
            targets = listOf(
                Offset(300f, 500f),
                Offset(700f, 1000f)
            ),
            interval = 1000L,
            cycles = 2,
            isRunning = true,
            clickCount = 2,
            totalClicks = 4
        )
    }
}
