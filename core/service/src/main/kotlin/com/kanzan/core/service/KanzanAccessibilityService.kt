package com.kanzan.core.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import com.kanzan.core.domain.model.AutoClickConfig
import com.kanzan.core.domain.model.Macro
import com.kanzan.core.service.engine.AutoClickExecutor
import com.kanzan.core.service.engine.GestureDispatcher
import com.kanzan.core.service.engine.TimingEngine
import com.kanzan.core.service.recording.TouchEventRecorder
import android.view.MotionEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Core accessibility service for gesture dispatch and auto-clicking
 * 
 * This service runs independently of the app process and provides
 * system-level gesture injection capabilities without root access
 */
class KanzanAccessibilityService : AccessibilityService() {
    
    // Service scope for coroutines
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    // Current execution job
    private var executionJob: Job? = null
    
    // Engine components
    private lateinit var gestureDispatcher: GestureDispatcher
    private lateinit var timingEngine: TimingEngine
    private lateinit var autoClickExecutor: AutoClickExecutor
    private lateinit var touchEventRecorder: TouchEventRecorder
    
    // State management
    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Idle)
    val serviceState: StateFlow<ServiceState> = _serviceState
    
    // Companion object for singleton access
    companion object {
        @Volatile
        private var instance: KanzanAccessibilityService? = null
        
        /**
         * Get current service instance if running
         */
        fun getInstance(): KanzanAccessibilityService? = instance
    }
    
    // ========================================
    // Lifecycle Methods
    // ========================================
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        
        // Store instance for global access
        instance = this
        
        // Initialize engine components
        gestureDispatcher = GestureDispatcher(this)
        timingEngine = TimingEngine()
        autoClickExecutor = AutoClickExecutor(gestureDispatcher)
        touchEventRecorder = TouchEventRecorder()
        
        // Configure service info
        serviceInfo = serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        }
        
        _serviceState.value = ServiceState.Idle
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used for recording - touch events captured via onTouchEvent
        // This method handles window state changes and system events
    }
    
    /**
     * Intercept touch events for recording
     * Called by RecordingOverlayService when capturing touches
     */
    fun onTouchEventCaptured(event: MotionEvent) {
        if (touchEventRecorder.isRecording()) {
            touchEventRecorder.recordEvent(event)
            
            // Update recording state with live metrics
            _serviceState.value = ServiceState.Recording(
                eventCount = touchEventRecorder.getEventCount(),
                duration = touchEventRecorder.getDuration()
            )
        }
    }
    
    override fun onInterrupt() {
        // Service was interrupted, stop current execution
        stopExecution()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Cleanup - check if initialized before stopping
        if (::autoClickExecutor.isInitialized) {
            stopExecution()
        }
        serviceScope.cancel()
        instance = null
        
        _serviceState.value = ServiceState.Idle
    }
    
    // ========================================
    // Macro Execution
    // ========================================
    
    /**
     * Execute a recorded macro
     * 
     * @param macro Macro to execute
     * @return Result indicating success or failure
     */
    fun executeMacro(macro: Macro): Result<Unit> {
        if (_serviceState.value.isExecuting()) {
            return Result.failure(IllegalStateException("Already executing"))
        }
        
        if (macro.events.isEmpty()) {
            return Result.failure(IllegalArgumentException("Macro has no events"))
        }
        
        executionJob = serviceScope.launch {
            try {
                _serviceState.value = ServiceState.Playing(
                    macroName = macro.name,
                    progress = 0f,
                    currentEvent = 0,
                    totalEvents = macro.events.size
                )
                
                timingEngine.reset()
                
                // Execute events sequentially
                macro.events.forEachIndexed { index, event ->
                    // Wait for accurate timing
                    timingEngine.waitUntil(event.timestamp)
                    
                    // Dispatch gesture for this event
                    // Note: Individual events need grouping into strokes
                    // For simplicity, dispatch as tap for now (proper implementation in Task 5)
                    val result = gestureDispatcher.dispatchTap(event.x, event.y)
                    
                    if (result.isFailure) {
                        _serviceState.value = ServiceState.Error(
                            message = "Failed to dispatch event ${index + 1}",
                            error = result.exceptionOrNull()
                        )
                        return@launch
                    }
                    
                    timingEngine.markDispatch()
                    
                    // Update progress
                    val progress = (index + 1).toFloat() / macro.events.size
                    _serviceState.value = ServiceState.Playing(
                        macroName = macro.name,
                        progress = progress,
                        currentEvent = index + 1,
                        totalEvents = macro.events.size
                    )
                }
                
                // Execution complete
                _serviceState.value = ServiceState.Idle
                
            } catch (e: Exception) {
                _serviceState.value = ServiceState.Error(
                    message = "Macro execution failed",
                    error = e
                )
            }
        }
        
        return Result.success(Unit)
    }
    
    // ========================================
    // Auto-Click Execution
    // ========================================
    
    /**
     * Execute auto-click configuration
     * 
     * @param config Auto-click configuration
     * @return Result indicating success or failure
     */
    fun executeAutoClick(config: AutoClickConfig): Result<Unit> {
        if (_serviceState.value.isExecuting()) {
            return Result.failure(IllegalStateException("Already executing"))
        }
        
        if (config.targets.isEmpty()) {
            return Result.failure(IllegalArgumentException("Config has no targets"))
        }
        
        executionJob = serviceScope.launch {
            try {
                _serviceState.value = ServiceState.AutoClicking(
                    configName = config.name,
                    clickCount = 0,
                    targetCount = config.repeatCount
                )
                
                val result = autoClickExecutor.execute(config) { clickCount, targetCount ->
                    // Update state on each click
                    _serviceState.value = ServiceState.AutoClicking(
                        configName = config.name,
                        clickCount = clickCount,
                        targetCount = targetCount
                    )
                }
                
                if (result.isFailure) {
                    _serviceState.value = ServiceState.Error(
                        message = "Auto-click execution failed",
                        error = result.exceptionOrNull()
                    )
                } else {
                    _serviceState.value = ServiceState.Idle
                }
                
            } catch (e: Exception) {
                _serviceState.value = ServiceState.Error(
                    message = "Auto-click execution failed",
                    error = e
                )
            }
        }
        
        return Result.success(Unit)
    }
    
    // ========================================
    // Execution Control
    // ========================================
    
    /**
     * Stop current execution
     */
    fun stopExecution() {
        executionJob?.cancel()
        executionJob = null
        autoClickExecutor.stop()
        _serviceState.value = ServiceState.Idle
    }
    
    /**
     * Pause current execution
     */
    fun pauseExecution() {
        val currentState = _serviceState.value
        if (currentState.isExecuting()) {
            executionJob?.cancel()
            _serviceState.value = ServiceState.Paused(currentState)
        }
    }
    
    /**
     * Resume paused execution
     */
    fun resumeExecution() {
        val currentState = _serviceState.value
        if (currentState is ServiceState.Paused) {
            // Resume from paused state
            // Implementation depends on what was paused (macro vs auto-click)
            // For now, just return to idle
            _serviceState.value = ServiceState.Idle
        }
    }
    
    // ========================================
    // Recording Control
    // ========================================
    
    /**
     * Start recording touch events
     */
    fun startRecording(): Result<Unit> {
        if (_serviceState.value.isExecuting()) {
            return Result.failure(IllegalStateException("Already executing"))
        }
        
        touchEventRecorder.startRecording()
        _serviceState.value = ServiceState.Recording(
            eventCount = 0,
            duration = 0
        )
        
        return Result.success(Unit)
    }
    
    /**
     * Stop recording and return captured events
     * 
     * @return List of recorded touch events
     */
    fun stopRecording(): List<com.kanzan.core.domain.model.TouchEvent> {
        val events = touchEventRecorder.stopRecording()
        _serviceState.value = ServiceState.Idle
        return events
    }
    
    /**
     * Cancel recording without saving
     */
    fun cancelRecording() {
        touchEventRecorder.cancelRecording()
        _serviceState.value = ServiceState.Idle
    }
    
    /**
     * Check if currently recording
     */
    fun isRecording(): Boolean = touchEventRecorder.isRecording()
    
    /**
     * Get recording duration in milliseconds
     */
    fun getRecordingDuration(): Long = touchEventRecorder.getDuration()
    
    // ========================================
    // State Queries
    // ========================================
    
    /**
     * Check if service is currently executing
     */
    fun isExecuting(): Boolean = _serviceState.value.isExecuting()
    
    /**
     * Get current execution progress (0.0 - 1.0)
     */
    fun getCurrentProgress(): Float {
        return when (val state = _serviceState.value) {
            is ServiceState.Playing -> state.progress
            is ServiceState.AutoClicking -> {
                if (state.targetCount <= 0) 0f
                else state.clickCount.toFloat() / state.targetCount
            }
            else -> 0f
        }
    }
    
    /**
     * Get current state
     */
    fun getState(): ServiceState = _serviceState.value
}
