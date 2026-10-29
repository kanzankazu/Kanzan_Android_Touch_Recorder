package com.kanzan.feature.macro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzan.core.ui.organisms.SaveMacroDialog
import com.kanzan.feature.macro.templates.*

/**
 * Screen: Macro list screen (top-level composition)
 */
@Composable
fun MacroListScreen(
    onNavigateToRecord: () -> Unit,
    onPlayMacro: (String) -> Unit,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MacroListViewModel = hiltViewModel(),
    recordingViewModel: RecordingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val recordingState by recordingViewModel.recordingState.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    
    // Show save dialog when recording stopped
    if (recordingState is RecordingUiState.ReadyToSave) {
        val events = (recordingState as RecordingUiState.ReadyToSave).events
        SaveMacroDialog(
            eventCount = events.size,
            duration = events.lastOrNull()?.timestamp ?: 0,
            onSave = { name, description ->
                recordingViewModel.saveMacro(events, name, description)
            },
            onDismiss = {
                recordingViewModel.dismissSaveDialog()
            }
        )
    }
    
    // Show playback feedback
    when (val state = playbackState) {
        is PlaybackState.Error -> {
            // Show error snackbar/toast
            androidx.compose.runtime.LaunchedEffect(state) {
                // TODO: Show snackbar with error message
                viewModel.resetPlaybackState()
            }
        }
        is PlaybackState.Playing -> {
            // Show playing feedback
            androidx.compose.runtime.LaunchedEffect(state) {
                // TODO: Show "Playing macro..." message
            }
        }
        else -> { /* Idle/Loading */ }
    }
    
    when (val state = uiState) {
        is MacroListUiState.Loading -> {
            MacroListLoadingTemplate(modifier = modifier)
        }
        
        is MacroListUiState.Empty -> {
            MacroListEmptyTemplate(
                onAddClick = onNavigateToRecord,
                onSettingsClick = onNavigateToSettings,
                modifier = modifier
            )
        }
        
        is MacroListUiState.Success -> {
            MacroListTemplate(
                macros = state.macros,
                onMacroClick = { macroId ->
                    // Trigger play via ViewModel instead of navigation callback
                    viewModel.playMacro(macroId)
                },
                onDeleteClick = { macroId ->
                    viewModel.deleteMacro(macroId)
                },
                onAddClick = onNavigateToRecord,
                onSettingsClick = onNavigateToSettings,
                modifier = modifier
            )
        }
        
        is MacroListUiState.Error -> {
            MacroListErrorTemplate(
                message = state.message,
                onRetryClick = { viewModel.retry() },
                modifier = modifier
            )
        }
    }
}
