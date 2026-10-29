package com.kanzan.touchrecorder

import android.app.Application
import com.kanzan.core.service.floating.FloatingOverlayService
import com.kanzan.core.service.recording.RecordingOverlayService
import com.kanzan.core.service.state.FloatingServiceStateManager
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class untuk Kanzan Touch Recorder
 * 
 * Menggunakan Hilt untuk dependency injection
 */
@HiltAndroidApp
class KanzanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Cleanup orphaned services from previous crash/force-close
        // If app restarts but SharedPreferences still shows service running,
        // it means app was force-closed or crashed - reset to clean state
        val stateManager = FloatingServiceStateManager.getInstance(this)
        if (stateManager.isServiceRunning.value) {
            // Service state persisted but app restarted - reset to Idle state
            FloatingOverlayService.stop(this)
            RecordingOverlayService.stop(this)
            stateManager.setServiceRunning(false)
        }
    }
}
