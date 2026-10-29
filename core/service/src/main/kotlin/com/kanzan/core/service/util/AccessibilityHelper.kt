package com.kanzan.core.service.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Helper object for accessibility service status checks and user guidance
 */
object AccessibilityHelper {
    
    /**
     * Check if KanzanAccessibilityService is currently enabled
     * 
     * @param context Android context
     * @return true if service is enabled, false otherwise
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        
        val serviceName = "${context.packageName}/com.kanzan.core.service.KanzanAccessibilityService"
        return enabledServices.contains(serviceName)
    }
    
    /**
     * Open system accessibility settings to allow user to enable service
     * 
     * @param context Android context
     */
    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
    
    /**
     * Observe accessibility service status as reactive Flow
     * 
     * Uses ContentObserver to watch Settings.Secure for changes
     * 
     * @param context Android context
     * @return Flow emitting true when enabled, false when disabled
     */
    fun observeAccessibilityStatus(context: Context): Flow<Boolean> = callbackFlow {
        val contentResolver = context.contentResolver
        val uri = Settings.Secure.getUriFor(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        
        // Initial emission
        trySend(isAccessibilityServiceEnabled(context))
        
        val observer = object : android.database.ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                trySend(isAccessibilityServiceEnabled(context))
            }
        }
        
        contentResolver.registerContentObserver(uri, false, observer)
        
        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }
    
    /**
     * Get user-friendly instruction string for enabling accessibility
     * 
     * @return Step-by-step instruction
     */
    fun getEnableInstructions(): String {
        return """
            To enable Kanzan Touch Recorder:
            
            1. Tap 'Open Settings' below
            2. Find 'Kanzan Touch Recorder' in the list
            3. Tap it and toggle ON
            4. Accept the permission dialog
            5. Return to the app
        """.trimIndent()
    }
}
