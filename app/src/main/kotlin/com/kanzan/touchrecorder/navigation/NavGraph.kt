package com.kanzan.touchrecorder.navigation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kanzan.core.service.floating.FloatingOverlayService
import com.kanzan.core.service.util.AccessibilityHelper
import com.kanzan.feature.macro.MacroListScreen

/**
 * App navigation graph
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionMessage by remember { mutableStateOf("") }
    
    // Permission check dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permission Required") },
            text = { Text(permissionMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        // Open settings based on permission type
                        if (permissionMessage.contains("Overlay")) {
                            // Open overlay permission settings
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        } else {
                            // Open accessibility settings
                            AccessibilityHelper.openAccessibilitySettings(context)
                        }
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    NavHost(
        navController = navController,
        startDestination = "macro_list",
        modifier = modifier
    ) {
        composable("macro_list") {
            MacroListScreen(
                onNavigateToRecord = {
                    // Check overlay permission
                    val hasOverlayPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Settings.canDrawOverlays(context)
                    } else {
                        true
                    }
                    
                    if (!hasOverlayPermission) {
                        permissionMessage = "Overlay permission is required to show floating controls during recording."
                        showPermissionDialog = true
                        return@MacroListScreen
                    }
                    
                    // Check accessibility service
                    val isAccessibilityEnabled = AccessibilityHelper.isAccessibilityServiceEnabled(context)
                    
                    if (!isAccessibilityEnabled) {
                        permissionMessage = "Accessibility Service is required to record and play gestures."
                        showPermissionDialog = true
                        return@MacroListScreen
                    }
                    
                    // Start floating overlay service
                    val intent = Intent(context, FloatingOverlayService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                },
                onPlayMacro = { }, // Handled by ViewModel now
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("settings") {
            com.kanzan.feature.settings.SettingsScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
