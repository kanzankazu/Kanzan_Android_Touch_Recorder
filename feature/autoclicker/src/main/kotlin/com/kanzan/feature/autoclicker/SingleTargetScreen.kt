package com.kanzan.feature.autoclicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzan.feature.autoclicker.templates.SingleTargetConfigTemplate
import com.kanzan.feature.autoclicker.templates.SingleTargetPickerTemplate

/**
 * Screen: Auto-click single target configuration
 */
@Composable
fun SingleTargetScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SingleTargetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is SingleTargetUiState.PickingTarget -> {
            SingleTargetPickerTemplate(
                onTargetPicked = { x, y ->
                    viewModel.setTarget(x, y)
                },
                onCancelClick = onNavigateBack,
                modifier = modifier
            )
        }
        
        is SingleTargetUiState.Configuring -> {
            SingleTargetConfigTemplate(
                target = state.target,
                interval = state.interval,
                count = state.count,
                onIntervalChange = { viewModel.setInterval(it) },
                onCountChange = { viewModel.setCount(it) },
                onStartClick = {
                    viewModel.startAutoClick()
                },
                onCancelClick = onNavigateBack,
                modifier = modifier
            )
        }
        
        is SingleTargetUiState.Running -> {
            SingleTargetConfigTemplate(
                target = state.target,
                interval = state.interval,
                count = state.count,
                isRunning = true,
                clickCount = state.clickCount,
                onStopClick = { viewModel.stopAutoClick() },
                onCancelClick = { /* Cannot cancel while running */ },
                modifier = modifier
            )
        }
    }
}
