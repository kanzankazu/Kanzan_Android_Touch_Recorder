package com.kanzan.feature.autoclicker.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
 * Template: Target picker for single auto-click
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleTargetPickerTemplate(
    onTargetPicked: (Float, Float) -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tapPosition by remember { mutableStateOf<Offset?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pick Target") },
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
                    OutlinedButton(
                        onClick = onCancelClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            tapPosition?.let { onTargetPicked(it.x, it.y) }
                        },
                        enabled = tapPosition != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Continue")
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
                .background(Color.Black.copy(alpha = 0.3f))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        tapPosition = offset
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "Tap anywhere on the screen\nto set click target",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                tapPosition?.let { pos ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Target Selected",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "X: ${pos.x.toInt()}  Y: ${pos.y.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Target Picker", showBackground = true)
@Composable
private fun SingleTargetPickerTemplatePreview() {
    KanzanTheme {
        SingleTargetPickerTemplate(
            onTargetPicked = { _, _ -> },
            onCancelClick = {}
        )
    }
}
