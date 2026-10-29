package com.kanzan.feature.autoclicker

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzan.core.domain.model.AutoClickConfig
import com.kanzan.core.domain.model.ClickTarget
import com.kanzan.core.domain.model.FreeTierLimits
import com.kanzan.core.domain.usecase.StartAutoClickUseCase
import com.kanzan.core.domain.usecase.StopAutoClickUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for multi-target auto-click
 */
@HiltViewModel
class MultiTargetViewModel @Inject constructor(
    private val startAutoClickUseCase: StartAutoClickUseCase,
    private val stopAutoClickUseCase: StopAutoClickUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<MultiTargetUiState>(
        MultiTargetUiState.PickingTargets(
            targets = emptyList(),
            maxTargets = FreeTierLimits().maxMultiTargets
        )
    )
    val uiState: StateFlow<MultiTargetUiState> = _uiState
    
    private var currentConfigId: String? = null
    
    fun addTarget(x: Float, y: Float) {
        val current = _uiState.value
        if (current is MultiTargetUiState.PickingTargets) {
            if (current.targets.size < current.maxTargets) {
                val newTarget = Offset(x, y)
                _uiState.value = current.copy(
                    targets = current.targets + newTarget
                )
            }
        }
    }
    
    fun removeTarget(index: Int) {
        val current = _uiState.value
        if (current is MultiTargetUiState.PickingTargets) {
            _uiState.value = current.copy(
                targets = current.targets.filterIndexed { i, _ -> i != index }
            )
        }
    }
    
    fun continueToConfig() {
        val current = _uiState.value
        if (current is MultiTargetUiState.PickingTargets && current.targets.isNotEmpty()) {
            _uiState.value = MultiTargetUiState.Configuring(
                targets = current.targets,
                interval = 1000L,
                cycles = 1
            )
        }
    }
    
    fun setInterval(interval: Long) {
        val current = _uiState.value
        if (current is MultiTargetUiState.Configuring) {
            _uiState.value = current.copy(interval = interval)
        }
    }
    
    fun setCycles(cycles: Int) {
        val current = _uiState.value
        if (current is MultiTargetUiState.Configuring) {
            _uiState.value = current.copy(cycles = cycles)
        }
    }
    
    fun startAutoClick() {
        val current = _uiState.value
        if (current is MultiTargetUiState.Configuring) {
            val configId = UUID.randomUUID().toString()
            currentConfigId = configId
            
            val config = AutoClickConfig(
                id = configId,
                name = "Multi Target",
                targets = current.targets.mapIndexed { index, offset ->
                    ClickTarget(
                        x = offset.x,
                        y = offset.y,
                        order = index
                    )
                },
                interval = current.interval,
                repeatCount = current.targets.size * current.cycles
            )
            
            viewModelScope.launch {
                startAutoClickUseCase(configId).onSuccess {
                    // Service execution handled by use case
                    _uiState.value = MultiTargetUiState.Running(
                        targets = current.targets,
                        interval = current.interval,
                        cycles = current.cycles,
                        clickCount = 0
                    )
                }
            }
        }
    }
    
    fun stopAutoClick() {
        currentConfigId?.let { configId ->
            viewModelScope.launch {
                stopAutoClickUseCase(configId)
                // Service stop handled by use case
                _uiState.value = MultiTargetUiState.PickingTargets(
                    targets = emptyList(),
                    maxTargets = FreeTierLimits().maxMultiTargets
                )
            }
        }
    }
}

/**
 * UI state for multi-target auto-click
 */
sealed class MultiTargetUiState {
    data class PickingTargets(
        val targets: List<Offset>,
        val maxTargets: Int
    ) : MultiTargetUiState()
    
    data class Configuring(
        val targets: List<Offset>,
        val interval: Long,
        val cycles: Int
    ) : MultiTargetUiState()
    
    data class Running(
        val targets: List<Offset>,
        val interval: Long,
        val cycles: Int,
        val clickCount: Int
    ) : MultiTargetUiState()
}
