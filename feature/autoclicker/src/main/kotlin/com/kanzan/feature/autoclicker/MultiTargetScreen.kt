package com.kanzan.feature.autoclicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzan.feature.autoclicker.templates.MultiTargetConfigTemplate
import com.kanzan.feature.autoclicker.templates.MultiTargetPickerTemplate

/**
 * Screen: Auto-click multi-target configuration
 */
@Composable
fun MultiTargetScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MultiTargetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is MultiTargetUiState.PickingTargets -> {
            MultiTargetPickerTemplate(
                targets = state.targets,
                maxTargets = state.maxTargets,
                onAddTarget = { x, y ->
                    viewModel.addTarget(x, y)
                },
                onRemoveTarget = { index ->
                    viewModel.removeTarget(index)
                },
                onContinueClick = {
                    viewModel.continueToConfig()
                },
                onCancelClick = onNavigateBack,
                modifier = modifier
            )
        }
        
        is MultiTargetUiState.Configuring -> {
            MultiTargetConfigTemplate(
                targets = state.targets,
                interval = state.interval,
                cycles = state.cycles,
                onIntervalChange = { viewModel.setInterval(it) },
                onCyclesChange = { viewModel.setCycles(it) },
                onStartClick = {
                    viewModel.startAutoClick()
                },
                onCancelClick = onNavigateBack,
                modifier = modifier
            )
        }
        
        is MultiTargetUiState.Running -> {
            MultiTargetConfigTemplate(
                targets = state.targets,
                interval = state.interval,
                cycles = state.cycles,
                isRunning = true,
                clickCount = state.clickCount,
                totalClicks = state.targets.size * state.cycles,
                onStopClick = { viewModel.stopAutoClick() },
                onCancelClick = { /* Cannot cancel while running */ },
                modifier = modifier
            )
        }
    }
}
