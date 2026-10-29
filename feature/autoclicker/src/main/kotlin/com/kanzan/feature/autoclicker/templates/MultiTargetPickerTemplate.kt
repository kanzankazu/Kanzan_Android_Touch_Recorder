package com.kanzan.feature.autoclicker.templates

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzan.core.designsystem.theme.KanzanTheme

/**
 * Template: Multi-target picker for auto-click
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiTargetPickerTemplate(
    targets: List<Offset>,
    maxTargets: Int,
    onAddTarget: (Float, Float) -> Unit,
    onRemoveTarget: (Int) -> Unit,
    onContinueClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pick Targets (${targets.size}/$maxTargets)") },
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
                Column {
                    // Target list
                    if (targets.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(targets) { index, target ->
                                TargetListItem(
                                    index = index,
                                    target = target,
                                    onRemoveClick = { onRemoveTarget(index) }
                                )
                            }
                        }
                        
                        Divider()
                    }
                    
                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = onContinueClick,
                            enabled = targets.isNotEmpty(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Continue")
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tap area with markers
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .pointerInput(targets, maxTargets) {
                        detectTapGestures { offset ->
                            if (targets.size < maxTargets) {
                                onAddTarget(offset.x, offset.y)
                            }
                        }
                    }
            ) {
                // Draw target markers
                targets.forEachIndexed { index, target ->
                    drawCircle(
                        color = Color.Red,
                        radius = 30f,
                        center = target
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3f,
                        center = target
                    )
                }
            }
            
            // Instructions
            if (targets.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tap to add targets\n(up to $maxTargets)",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetListItem(
    index: Int,
    target: Offset,
    onRemoveClick: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Target ${index + 1}",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "X: ${target.x.toInt()}  Y: ${target.y.toInt()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove target",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(name = "Multi Target Picker - Empty", showBackground = true)
@Composable
private fun MultiTargetPickerTemplateEmptyPreview() {
    KanzanTheme {
        MultiTargetPickerTemplate(
            targets = emptyList(),
            maxTargets = 3,
            onAddTarget = { _, _ -> },
            onRemoveTarget = {},
            onContinueClick = {},
            onCancelClick = {}
        )
    }
}

@Preview(name = "Multi Target Picker - With Targets", showBackground = true)
@Composable
private fun MultiTargetPickerTemplateWithTargetsPreview() {
    KanzanTheme {
        MultiTargetPickerTemplate(
            targets = listOf(
                Offset(300f, 500f),
                Offset(700f, 1000f)
            ),
            maxTargets = 3,
            onAddTarget = { _, _ -> },
            onRemoveTarget = {},
            onContinueClick = {},
            onCancelClick = {}
        )
    }
}
