package com.kanzan.feature.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzan.core.domain.model.UserTier
import com.kanzan.core.domain.usecase.CheckAccessibilityUseCase
import com.kanzan.core.domain.usecase.GetUserTierUseCase
import com.kanzan.core.service.floating.FloatingOverlayService
import com.kanzan.core.service.state.FloatingServiceStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for settings screen
 * 
 * Uses FloatingServiceStateManager for centralized floating service state tracking
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getUserTierUseCase: GetUserTierUseCase,
    checkAccessibilityUseCase: CheckAccessibilityUseCase
) : ViewModel() {
    
    // Use centralized state manager instead of local state
    private val floatingStateManager = FloatingServiceStateManager.getInstance(context)

    val uiState: StateFlow<SettingsUiState> = combine(
        getUserTierUseCase(),
        checkAccessibilityUseCase(),
        floatingStateManager.isServiceRunning // Use centralized StateFlow
    ) { tier, isAccessibilityEnabled, isFloatingServiceRunning ->
        SettingsUiState(
            userTier = tier,
            isAccessibilityEnabled = isAccessibilityEnabled,
            isFloatingServiceRunning = isFloatingServiceRunning
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleFloatingService(context: Context, enable: Boolean) {
        viewModelScope.launch {
            if (enable) {
                // Start service (service will update StateManager in onCreate)
                FloatingOverlayService.start(context)
            } else {
                // Stop service (service will update StateManager in onDestroy)
                FloatingOverlayService.stop(context)
            }
            // Note: No manual state update needed - FloatingOverlayService handles it
        }
    }
}

data class SettingsUiState(
    val userTier: UserTier = UserTier.FREE,
    val isAccessibilityEnabled: Boolean = false,
    val isFloatingServiceRunning: Boolean = false
)
