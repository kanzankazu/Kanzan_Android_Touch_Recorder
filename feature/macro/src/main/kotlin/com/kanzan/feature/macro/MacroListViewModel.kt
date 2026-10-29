package com.kanzan.feature.macro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzan.core.domain.model.Macro
import com.kanzan.core.domain.usecase.DeleteMacroUseCase
import com.kanzan.core.domain.usecase.GetAllMacrosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for macro list screen
 */
@HiltViewModel
class MacroListViewModel @Inject constructor(
    private val getAllMacrosUseCase: GetAllMacrosUseCase,
    private val deleteMacroUseCase: DeleteMacroUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<MacroListUiState>(MacroListUiState.Loading)
    val uiState: StateFlow<MacroListUiState> = _uiState
    
    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState
    
    init {
        loadMacros()
    }
    
    private fun loadMacros() {
        viewModelScope.launch {
            getAllMacrosUseCase()
                .catch { error ->
                    _uiState.value = MacroListUiState.Error(
                        error.message ?: "Failed to load macros"
                    )
                }
                .collectLatest { macros ->
                    _uiState.value = if (macros.isEmpty()) {
                        MacroListUiState.Empty
                    } else {
                        MacroListUiState.Success(macros)
                    }
                }
        }
    }
    
    fun playMacro(macroId: String) {
        viewModelScope.launch {
            try {
                _playbackState.value = PlaybackState.Loading
                
                // Get accessibility service instance
                val service = com.kanzan.core.service.KanzanAccessibilityService.getInstance()
                if (service == null) {
                    _playbackState.value = PlaybackState.Error("Accessibility Service not running")
                    return@launch
                }
                
                // Get macro from current state
                val currentState = _uiState.value
                if (currentState !is MacroListUiState.Success) {
                    _playbackState.value = PlaybackState.Error("Macros not loaded")
                    return@launch
                }
                
                val macro = currentState.macros.find { it.id == macroId }
                if (macro == null) {
                    _playbackState.value = PlaybackState.Error("Macro not found")
                    return@launch
                }
                
                // Execute macro via service
                val result = service.executeMacro(macro)
                if (result.isSuccess) {
                    _playbackState.value = PlaybackState.Playing(macroId)
                } else {
                    _playbackState.value = PlaybackState.Error(
                        result.exceptionOrNull()?.message ?: "Failed to play macro"
                    )
                }
                
            } catch (e: Exception) {
                _playbackState.value = PlaybackState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun deleteMacro(macroId: String) {
        viewModelScope.launch {
            deleteMacroUseCase(macroId)
                .onFailure { error ->
                    _uiState.value = MacroListUiState.Error(
                        error.message ?: "Failed to delete macro"
                    )
                }
        }
    }
    
    fun retry() {
        _uiState.value = MacroListUiState.Loading
        loadMacros()
    }
    
    fun resetPlaybackState() {
        _playbackState.value = PlaybackState.Idle
    }
}

/**
 * UI state for macro list screen
 */
sealed class MacroListUiState {
    object Loading : MacroListUiState()
    object Empty : MacroListUiState()
    data class Success(val macros: List<Macro>) : MacroListUiState()
    data class Error(val message: String) : MacroListUiState()
}

/**
 * Playback state for macro execution
 */
sealed class PlaybackState {
    object Idle : PlaybackState()
    object Loading : PlaybackState()
    data class Playing(val macroId: String) : PlaybackState()
    data class Error(val message: String) : PlaybackState()
}
