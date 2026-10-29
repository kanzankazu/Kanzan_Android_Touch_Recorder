package com.kanzan.feature.macro.templates

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzan.core.designsystem.theme.KanzanTheme
import com.kanzan.core.domain.model.Macro
import com.kanzan.core.service.floating.FloatingOverlayService
import com.kanzan.core.service.state.FloatingServiceStateManager
import com.kanzan.feature.macro.organisms.MacroCard

/**
 * Template: Macro list layout template
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroListTemplate(
    macros: List<Macro>,
    onMacroClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val stateManager = remember { FloatingServiceStateManager.getInstance(context) }
    val isFloatingEnabled by stateManager.isServiceRunning.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Macros") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add macro"
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Floating Controls Switch - First Item
            item {
                FloatingControlCard(
                    isEnabled = isFloatingEnabled,
                    onToggle = { enabled ->
                        if (enabled) {
                            FloatingOverlayService.start(context)
                        } else {
                            FloatingOverlayService.stop(context)
                        }
                    }
                )
            }
            
            // Macro List Items
            items(
                items = macros,
                key = { it.id }
            ) { macro ->
                MacroCard(
                    macro = macro,
                    onPlayClick = onMacroClick,
                    onDeleteClick = onDeleteClick
                )
            }
        }
    }
}

/**
 * Floating Control Card Component
 */
@Composable
private fun FloatingControlCard(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Floating Controls",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isEnabled) "Overlay active - tap to control" else "Enable overlay for quick access",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
            
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

/**
 * Empty state template
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroListEmptyTemplate(
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val stateManager = remember { FloatingServiceStateManager.getInstance(context) }
    val isFloatingEnabled by stateManager.isServiceRunning.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Macros") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add macro"
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Floating Controls Switch - Always on top
            FloatingControlCard(
                isEnabled = isFloatingEnabled,
                onToggle = { enabled ->
                    if (enabled) {
                        FloatingOverlayService.start(context)
                    } else {
                        FloatingOverlayService.stop(context)
                    }
                }
            )
            
            // Empty State Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "No macros yet",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Tap + to record your first macro",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Loading state template
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroListLoadingTemplate(
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Macros") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

/**
 * Error state template
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroListErrorTemplate(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Macros") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Button(onClick = onRetryClick) {
                    Text("Retry")
                }
            }
        }
    }
}

// Previews
@Preview(name = "Macro List - Success", showBackground = true)
@Composable
private fun MacroListTemplatePreview() {
    KanzanTheme {
        MacroListTemplate(
            macros = listOf(
                Macro("1", "Swipe Up", "Quick swipe", emptyList(), 0, 0, 350),
                Macro("2", "Long Press", "", emptyList(), 0, 0, 1000)
            ),
            onMacroClick = {},
            onDeleteClick = {},
            onAddClick = {}
        )
    }
}

@Preview(name = "Macro List - Empty", showBackground = true)
@Composable
private fun MacroListEmptyTemplatePreview() {
    KanzanTheme {
        MacroListEmptyTemplate(onAddClick = {})
    }
}

@Preview(name = "Macro List - Loading", showBackground = true)
@Composable
private fun MacroListLoadingTemplatePreview() {
    KanzanTheme {
        MacroListLoadingTemplate()
    }
}

@Preview(name = "Macro List - Error", showBackground = true)
@Composable
private fun MacroListErrorTemplatePreview() {
    KanzanTheme {
        MacroListErrorTemplate(
            message = "Failed to load macros. Please try again.",
            onRetryClick = {}
        )
    }
}
