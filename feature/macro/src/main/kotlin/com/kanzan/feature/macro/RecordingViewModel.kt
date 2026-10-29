package com.kanzan.feature.macro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzan.core.domain.model.TouchEvent
import com.kanzan.core.domain.usecase.RecordMacroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for recording operations
 */
@HiltViewModel
class RecordingViewModel @Inject constructor(
    private val recordMacroUseCase: RecordMacroUseCase
) : ViewModel() {
    
    private val _recordingState = MutableStateFlow<RecordingUiState>(RecordingUiState.Idle)
    val recordingState: StateFlow<RecordingUiState> = _recordingState
    
    // Note: Service state observation removed - will be handled by floating overlay
    // Recording state managed manually via start/stop/save methods
    
    /**
     * Trigger save dialog with recorded events
     */
    fun showSaveDialog(events: List<TouchEvent>) {
        if (events.isNotEmpty()) {
            _recordingState.value = RecordingUiState.ReadyToSave(events)
        } else {
            _recordingState.value = RecordingUiState.Error("No events recorded")
        }
    }
    
    /**
     * Save recorded macro
     */
    fun saveMacro(events: List<TouchEvent>, name: String, description: String) {
        viewModelScope.launch {
            _recordingState.value = RecordingUiState.Saving
            
            recordMacroUseCase(events, name, description)
                .onSuccess {
                    _recordingState.value = RecordingUiState.Saved
                }
                .onFailure { error ->
                    _recordingState.value = RecordingUiState.Error(
                        error.message ?: "Failed to save macro"
                    )
                }
        }
    }
    
    /**
     * Cancel and discard recorded events
     */
    fun cancelRecording() {
        _recordingState.value = RecordingUiState.Idle
    }
    
    /**
     * Dismiss save dialog
     */
    fun dismissSaveDialog() {
        _recordingState.value = RecordingUiState.Idle
    }
    
    /**
     * Reset to idle state
     */
    fun resetState() {
        _recordingState.value = RecordingUiState.Idle
    }
}

/**
 * UI state for recording
 */
sealed class RecordingUiState {
    object Idle : RecordingUiState()
    
    data class Recording(
        val eventCount: Int,
        val duration: Long
    ) : RecordingUiState()
    
    data class ReadyToSave(
        val events: List<TouchEvent>
    ) : RecordingUiState()
    
    object Saving : RecordingUiState()
    object Saved : RecordingUiState()
    
    data class Error(
        val message: String
    ) : RecordingUiState()
}
