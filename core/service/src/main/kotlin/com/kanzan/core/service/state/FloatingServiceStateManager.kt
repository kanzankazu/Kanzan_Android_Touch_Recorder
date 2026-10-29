package com.kanzan.core.service.state

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Centralized state manager for floating service lifecycle
 * 
 * Replaces deprecated ActivityManager.getRunningServices() API with
 * SharedPreferences + reactive StateFlow for reliable state tracking
 * 
 * Thread-safe singleton implementation
 */
class FloatingServiceStateManager private constructor(context: Context) {
    
    private val prefs: SharedPreferences = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _isServiceRunning = MutableStateFlow(prefs.getBoolean(KEY_SERVICE_RUNNING, false))
    
    /**
     * Observable state of FloatingOverlayService
     * 
     * Emits true when service is running, false when stopped
     * UI screens should collect this flow for reactive updates
     */
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()
    
    /**
     * Update service running state
     * 
     * Called by FloatingOverlayService in onCreate/onDestroy lifecycle
     * Updates persistent storage and notifies all observers
     * 
     * @param running true if service starting, false if stopping
     */
    fun setServiceRunning(running: Boolean) {
        // Persist to SharedPreferences for crash recovery
        prefs.edit()
            .putBoolean(KEY_SERVICE_RUNNING, running)
            .apply()
        
        // Emit to StateFlow for reactive UI updates
        _isServiceRunning.value = running
    }
    
    companion object {
        private const val PREFS_NAME = "floating_service_state"
        private const val KEY_SERVICE_RUNNING = "is_service_running"
        
        @Volatile
        private var instance: FloatingServiceStateManager? = null
        
        /**
         * Get singleton instance of FloatingServiceStateManager
         * 
         * Thread-safe double-checked locking implementation
         * 
         * @param context Application or activity context (will use applicationContext)
         * @return Singleton instance
         */
        fun getInstance(context: Context): FloatingServiceStateManager {
            return instance ?: synchronized(this) {
                instance ?: FloatingServiceStateManager(context).also { instance = it }
            }
        }
    }
}
