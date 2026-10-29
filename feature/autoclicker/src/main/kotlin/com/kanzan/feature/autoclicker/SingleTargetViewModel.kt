package com.kanzan.feature.autoclicker

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzan.core.domain.model.AutoClickConfig
import com.kanzan.core.domain.model.ClickTarget
import com.kanzan.core.domain.usecase.StartAutoClickUseCase
import com.kanzan.core.domain.usecase.StopAutoClickUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for single target auto-click
 */
@HiltViewModel
class SingleTargetViewModel @Inject constructor(
    private val startAutoClickUseCase: StartAutoClickUseCase,
    private val stopAutoClickUseCase: StopAutoClickUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SingleTargetUiState>(
        SingleTargetUiState.PickingTarget
    )
    val uiState: StateFlow<SingleTargetUiState> = _uiState
    
    private var currentConfigId: String? = null
    
    fun setTarget(x: Float, y: Float) {
        _uiState.value = SingleTargetUiState.Configuring(
            target = Offset(x, y),
            interval = 1000L,
            count = 10
        )
    }
    
    fun setInterval(interval: Long) {
        val current = _uiState.value
        if (current is SingleTargetUiState.Configuring) {
            _uiState.value = current.copy(interval = interval)
        }
    }
    
    fun setCount(count: Int) {
        val current = _uiState.value
        if (current is SingleTargetUiState.Configuring) {
            _uiState.value = current.copy(count = count)
        }
    }
    
    fun startAutoClick() {
        val current = _uiState.value
        if (current is SingleTargetUiState.Configuring) {
            val configId = UUID.randomUUID().toString()
            currentConfigId = configId
            
            val config = AutoClickConfig(
                id = configId,
                name = "Single Target",
                targets = listOf(
                    ClickTarget(
                        x = current.target.x,
                        y = current.target.y,
                        order = 0
                    )
                ),
                interval = current.interval,
                repeatCount = current.count
            )
            
            viewModelScope.launch {
                startAutoClickUseCase(configId).onSuccess {
                    // Service execution handled by use case
                    _uiState.value = SingleTargetUiState.Running(
                        target = current.target,
                        interval = current.interval,
                        count = current.count,
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
                _uiState.value = SingleTargetUiState.PickingTarget
            }
        }
    }
}

/**
 * UI state for single target auto-click
 */
sealed class SingleTargetUiState {
    object PickingTarget : SingleTargetUiState()
    
    data class Configuring(
        val target: Offset,
        val interval: Long,
        val count: Int
    ) : SingleTargetUiState()
    
    data class Running(
        val target: Offset,
        val interval: Long,
        val count: Int,
        val clickCount: Int
    ) : SingleTargetUiState()
}
