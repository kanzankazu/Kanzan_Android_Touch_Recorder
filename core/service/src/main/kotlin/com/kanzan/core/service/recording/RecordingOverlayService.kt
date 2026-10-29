package com.kanzan.core.service.recording

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.kanzan.core.service.KanzanAccessibilityService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

/**
 * Invisible fullscreen overlay to capture touch events during recording
 * 
 * This service creates a transparent overlay that intercepts all touch events
 * and passes them to TouchEventRecorder. Uses dispatchTouchEvent override
 * instead of FLAG_NOT_TOUCHABLE to properly capture touches.
 * 
 * Implements foreground service for Android 8.0+ with auto-timeout at 5 minutes.
 */
class RecordingOverlayService : Service() {
    
    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timeoutJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        
        // Start foreground service IMMEDIATELY (required for Android 8.0+)
        createNotificationChannel()
        val notification = createRecordingNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createInvisibleOverlay()
        
        // Start 5-minute timeout
        startTimeout()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recording Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Captures touch events during recording"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createRecordingNotification(): android.app.Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Kanzan Recording")
            .setContentText("Touch events are being recorded")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createInvisibleOverlay() {
        // Create custom view with dispatchTouchEvent override
        overlayView = object : View(this) {
            override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                // Pass event to KanzanAccessibilityService for recording
                KanzanAccessibilityService.getInstance()?.onTouchEventCaptured(event)
                // Return false to pass touch through to apps below
                return false
            }
        }

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            // CRITICAL FIX: Removed FLAG_NOT_TOUCHABLE
            // FLAG_NOT_TOUCHABLE blocks ALL touch interception including setOnTouchListener
            // Using only FLAG_NOT_FOCUSABLE allows dispatchTouchEvent to intercept touches
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        windowManager.addView(overlayView, layoutParams)
    }
    
    private fun startTimeout() {
        timeoutJob = serviceScope.launch {
            delay(5.minutes)
            // Auto-stop recording after 5 minutes
            KanzanAccessibilityService.getInstance()?.stopRecording()
            stop(this@RecordingOverlayService)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // App swiped from recents - stop recording overlay
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        timeoutJob?.cancel()
        serviceScope.cancel()
        overlayView?.let { windowManager.removeView(it) }
        overlayView = null
    }

    override fun onBind(intent: Intent): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "kanzan_recording_overlay"
        private const val NOTIFICATION_ID = 2001
        
        fun start(context: Context) {
            val intent = Intent(context, RecordingOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, RecordingOverlayService::class.java)
            context.stopService(intent)
        }
    }
}
