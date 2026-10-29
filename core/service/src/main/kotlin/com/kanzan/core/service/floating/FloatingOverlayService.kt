package com.kanzan.core.service.floating

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import com.kanzan.core.service.recording.RecordingOverlayService
import com.kanzan.core.service.state.FloatingServiceStateManager
import com.kanzan.core.service.ServiceState
import com.kanzan.core.service.toIcon
import com.kanzan.core.service.toIconColor
import com.kanzan.core.service.shouldPulseIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Service that displays floating overlay UI using Compose
 * 
 * Based on proven implementation pattern from production apps
 */
class FloatingOverlayService : Service(), LifecycleOwner, SavedStateRegistryOwner {
    
    private lateinit var windowManager: WindowManager
    private var overlayView: ComposeView? = null
    private lateinit var layoutParams: WindowManager.LayoutParams
    
    private val overlayState = MutableStateFlow(OverlayState())
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Track drag offset for WindowManager updates
    private var overlayOffset by mutableStateOf(Offset.Zero)
    
    // Lifecycle support
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    
    override fun onCreate() {
        super.onCreate()
        
        // Start foreground service IMMEDIATELY (required for Android 8.0+)
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Register service state for centralized tracking
        FloatingServiceStateManager.getInstance(this).setServiceRunning(true)
        
        // Initialize lifecycle
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        createOverlayView()
        observeAccessibilityService()
        
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Floating Controls",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows floating overlay controls for quick access"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): android.app.Notification {
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Kanzan Touch Recorder")
            .setContentText("Floating controls active - tap to expand")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
    
    private fun createOverlayView() {
        overlayView = ComposeView(this).apply {
            // IMPORTANT: Set lifecycle owners BEFORE setting content
            setViewTreeLifecycleOwner(this@FloatingOverlayService)
            setViewTreeSavedStateRegistryOwner(this@FloatingOverlayService)
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
            x = 0
            y = 200
        }
        
        // Add view to window manager BEFORE setting content
        windowManager.addView(overlayView, layoutParams)
        
        // NOW set content after view is attached and lifecycle is set
        overlayView?.setContent {
            val state by overlayState.collectAsState()
            
            KanzanTheme {
                if (state.isExpanded) {
                    // Expanded panel (not draggable)
                    FloatingPanel(
                        serviceState = state.serviceState,
                        onPlayClick = { handlePlay() },
                        onRecordClick = { handleRecord() },
                        onStopClick = { handleStop() },
                        onCloseClick = { toggleExpanded() },
                        onHideClick = { handleHide() }
                    )
                } else {
                    // Collapsed button (draggable)
                    DraggableFloatingButton(
                        serviceState = state.serviceState,
                        onTap = { toggleExpanded() }
                    )
                }
            }
        }
    }
    
    @Composable
    private fun DraggableFloatingButton(serviceState: com.kanzan.core.service.ServiceState, onTap: () -> Unit) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .pointerInput(Unit) {
                    // Handle drag gestures
                    detectDragGestures { change, dragAmount ->
                        // Update offset
                        val newOffset = overlayOffset + dragAmount
                        overlayOffset = newOffset
                        
                        // Update WindowManager layout params
                        layoutParams.apply {
                            x = overlayOffset.x.roundToInt()
                            y = overlayOffset.y.roundToInt()
                        }
                        overlayView?.let { windowManager.updateViewLayout(it, layoutParams) }
                    }
                }
                .pointerInput(Unit) {
                    // Handle tap gesture (separate from drag)
                    detectTapGestures {
                        onTap()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Pulse animation for active states (Recording, AutoClicking)
            val infiniteTransition = rememberInfiniteTransition(label = "icon_pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = if (serviceState.shouldPulseIcon()) 0.3f else 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            
            Icon(
                imageVector = serviceState.toIcon(),
                contentDescription = when (serviceState) {
                    is com.kanzan.core.service.ServiceState.Recording -> "Recording"
                    is com.kanzan.core.service.ServiceState.Playing -> "Playing"
                    is com.kanzan.core.service.ServiceState.AutoClicking -> "Auto-clicking"
                    else -> "Open panel"
                },
                tint = serviceState.toIconColor().takeIf { it != androidx.compose.ui.graphics.Color.Unspecified }
                    ?: MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(32.dp)
                    .then(
                        if (serviceState.shouldPulseIcon()) Modifier.graphicsLayer(alpha = alpha)
                        else Modifier
                    )
            )
        }
    }
    
    private fun observeAccessibilityService() {
        serviceScope.launch {
            KanzanAccessibilityService.getInstance()?.serviceState?.collect { state ->
                overlayState.value = overlayState.value.copy(serviceState = state)
            }
        }
    }
    
    private fun toggleExpanded() {
        overlayState.value = overlayState.value.copy(
            isExpanded = !overlayState.value.isExpanded
        )
    }
    
    private fun handlePlay() {
        // Launch main app to show macro list for selection
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("ACTION_PLAY_MACRO", true)
        }
        
        if (intent != null) {
            startActivity(intent)
        }
        
        toggleExpanded()
    }
    
    private fun handleRecord() {
        val service = KanzanAccessibilityService.getInstance()
        if (service != null) {
            if (service.isRecording()) {
                // Stop recording
                val events = service.stopRecording()
                
                // Stop recording overlay
                RecordingOverlayService.stop(this)
                
                // Launch app to show save dialog if events recorded
                if (events.isNotEmpty()) {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        putExtra("ACTION_SAVE_RECORDING", true)
                        putExtra("EVENT_COUNT", events.size)
                    }
                    
                    if (intent != null) {
                        startActivity(intent)
                    }
                }
            } else {
                // Start recording
                val result = service.startRecording()
                if (result.isSuccess) {
                    // Start invisible recording overlay to capture touches
                    RecordingOverlayService.start(this)
                }
            }
        }
        toggleExpanded()
    }
    
    private fun handleStop() {
        val service = KanzanAccessibilityService.getInstance()
        
        // Stop any ongoing execution
        service?.stopExecution()
        
        // If was recording, stop recording overlay
        if (service?.isRecording() == true) {
            RecordingOverlayService.stop(this)
        }
    }
    
    private fun handleHide() {
        // Stop the floating overlay service
        stop(this)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Handle lifecycle destruction
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        
        // Unregister service state
        FloatingServiceStateManager.getInstance(this).setServiceRunning(false)

        serviceScope.cancel()
        overlayView?.let { windowManager.removeView(it) }
        overlayView = null
    }
    
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // App swiped from recents - cleanup recording overlay if active
        RecordingOverlayService.stop(this)
        // Stop this service to avoid orphaned process
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    
    companion object {
        private const val CHANNEL_ID = "floating_controls"
        private const val NOTIFICATION_ID = 1001
        
        fun start(context: Context) {
            val intent = Intent(context, FloatingOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            val intent = Intent(context, FloatingOverlayService::class.java)
            context.stopService(intent)
        }
    }
}
