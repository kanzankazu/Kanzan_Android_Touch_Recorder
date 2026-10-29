package com.kanzan.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzan.core.domain.model.UserTier
import com.kanzan.core.service.util.AccessibilityHelper

/**
 * Settings screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showUpgradeDialog by remember { mutableStateOf(false) }
    
    // Upgrade dialog
    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            title = { Text("Upgrade to Premium") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Premium features:", style = MaterialTheme.typography.titleSmall)
                    Text("• Unlimited recording time")
                    Text("• Unlimited macros")
                    Text("• No ads")
                    Text("• Priority support")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // TODO: Integrate Google Play Billing
                        showUpgradeDialog = false
                    }
                ) {
                    Text("Upgrade Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpgradeDialog = false }) {
                    Text("Maybe Later")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Accessibility Service
            SettingsItem(
                title = "Accessibility Service",
                subtitle = if (uiState.isAccessibilityEnabled) "Enabled" else "Disabled",
                onClick = {
                    AccessibilityHelper.openAccessibilitySettings(context)
                }
            )
            
            Divider()
            
            // User Tier
            SettingsItem(
                title = "Subscription",
                subtitle = when (uiState.userTier) {
                    UserTier.FREE -> "Free (with ads)"
                    UserTier.PREMIUM -> "Premium"
                },
                onClick = {
                    // Show upgrade dialog for free users
                    if (uiState.userTier == UserTier.FREE) {
                        showUpgradeDialog = true
                    }
                }
            )
            
            Divider()
            
            // Floating Overlay
            SettingSwitchItem(
                title = "Floating Controls",
                subtitle = if (uiState.isFloatingServiceRunning) "Controls active" else "Controls inactive",
                checked = uiState.isFloatingServiceRunning,
                onCheckedChange = { enabled ->
                    viewModel.toggleFloatingService(context, enabled)
                }
            )
            
            Divider()
            
            // App Version
            SettingsItem(
                title = "App Version",
                subtitle = "1.0.0"
            )
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
