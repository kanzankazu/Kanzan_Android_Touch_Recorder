package com.kanzan.core.service.recording

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.kanzan.core.designsystem.theme.KanzanTheme
import com.kanzan.core.service.KanzanAccessibilityService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes

/**
 * MANUAL TOUCH RECORDING OVERLAY
 * 
 * Shows draggable "+" button - user positions it and taps to record touch point.
 * This is the ONLY reliable way to get accurate coordinates without blocking UI.
 */
class RecordingOverlayService : Service(), LifecycleOwner, SavedStateRegistryOwner {
    
    private lateinit var windowManager: WindowManager
    private var overlayView: ComposeView? = null
    private lateinit var layoutParams: WindowManager.LayoutParams
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timeoutJob: Job? = null
    
    // Lifecycle support
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    
    // Button position state
    private var buttonX by mutableStateOf(0)
    private var buttonY by mutableStateOf(0)
    private var touchCount = 0

    override fun onCreate() {
        super.onCreate()
        
        Log.d(TAG, "RecordingOverlayService onCreate() - MANUAL RECORDING MODE")
        
        // Start foreground service
        createNotificationChannel()
        val notification = createRecordingNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        // Initialize lifecycle
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        // Get screen dimensions for initial positioning
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        buttonX = displayMetrics.widthPixels / 2 - 28.dp.value.toInt() // Center
        buttonY = displayMetrics.heightPixels / 2 - 28.dp.value.toInt()
        
        createRecordingButton()
        
        // Start timeout
        startTimeout()
        
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        
        Log.d(TAG, "📍 MANUAL RECORDING: Drag + button to target, tap to record touch")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Manual Recording",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tap + button to record touch points"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createRecordingNotification(): android.app.Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Kanzan Recording")
            .setContentText("Drag + button, tap to record touch (${touchCount} recorded)")
            .setSmallIcon(android.R.drawable.ic_input_add)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createRecordingButton() {
        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@RecordingOverlayService)
            setViewTreeSavedStateRegistryOwner(this@RecordingOverlayService)
        }

        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = buttonX
            y = buttonY
        }

        windowManager.addView(overlayView, layoutParams)

        overlayView?.setContent {
            KanzanTheme {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .background(
                            color = Color(0xFFFF6B35), // Orange for recording
                            shape = CircleShape
                        )
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                
                                // Update button position
                                val newX = (layoutParams.x + dragAmount.x).roundToInt()
                                val newY = (layoutParams.y + dragAmount.y).roundToInt()
                                
                                layoutParams.apply {
                                    x = newX
                                    y = newY
                                }
                                
                                overlayView?.let { 
                                    windowManager.updateViewLayout(it, layoutParams)
                                }
                                
                                buttonX = newX
                                buttonY = newY
                            }
                        }
                        .pointerInput(Unit) {
                            // Detect tap (not drag) to record touch
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.changes.first().pressed) {
                                        val change = event.changes.first()
                                        if (!change.previousPressed) {
                                            // This is initial press (tap)
                                            recordTouchAtCurrentPosition()
                                        }
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Record touch here",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
    
    private fun recordTouchAtCurrentPosition() {
        touchCount++
        
        val screenX = layoutParams.x + 28 // Center of 56dp button
        val screenY = layoutParams.y + 28
        
        Log.d(TAG, "✅ Recording touch #$touchCount at ($screenX, $screenY)")
        
        // Create synthetic touch event at button center
        val downEvent = MotionEvent.obtain(
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            MotionEvent.ACTION_DOWN,
            screenX.toFloat(),
            screenY.toFloat(),
            0
        )
        
        val upEvent = MotionEvent.obtain(
            System.currentTimeMillis(),
            System.currentTimeMillis() + 50,
            MotionEvent.ACTION_UP,
            screenX.toFloat(),
            screenY.toFloat(),
            0
        )
        
        // Send to recorder
        KanzanAccessibilityService.getInstance()?.apply {
            onTouchEventCaptured(downEvent)
            onTouchEventCaptured(upEvent)
        }
        
        downEvent.recycle()
        upEvent.recycle()
        
        // Update notification
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createRecordingNotification())
    }
    
    private fun startTimeout() {
        timeoutJob = serviceScope.launch {
            delay(5.minutes)
            Log.d(TAG, "Recording timeout reached")
            KanzanAccessibilityService.getInstance()?.stopRecording()
            stop(this@RecordingOverlayService)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "RecordingOverlayService onDestroy() - recorded $touchCount touches")
        
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        
        timeoutJob?.cancel()
        serviceScope.cancel()
        overlayView?.let { windowManager.removeView(it) }
        overlayView = null
    }

    override fun onBind(intent: Intent): IBinder? = null

    companion object {
        private const val TAG = "RecordingOverlay"
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
