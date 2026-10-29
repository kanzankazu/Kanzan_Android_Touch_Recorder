package com.kanzan.core.designsystem.atoms

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.kanzan.core.designsystem.theme.KanzanTheme

/**
 * Progress indicator types
 */
enum class ProgressType {
    Circular,
    Linear
}

/**
 * Reusable progress indicator component
 *
 * Supports circular and linear variants, dengan determinate/indeterminate states
 *
 * @param modifier Modifier for customization
 * @param type Circular atau Linear
 * @param progress Progress value (0.0 to 1.0), null untuk indeterminate
 * @param color Progress indicator color
 */
@Composable
fun AppProgressIndicator(
    modifier: Modifier = Modifier,
    type: ProgressType = ProgressType.Circular,
    progress: Float? = null,
    color: Color? = null
) {
    val indicatorColor = color ?: MaterialTheme.colorScheme.primary
    
    when (type) {
        ProgressType.Circular -> {
            if (progress != null) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = modifier,
                    color = indicatorColor
                )
            } else {
                CircularProgressIndicator(
                    modifier = modifier,
                    color = indicatorColor
                )
            }
        }
        
        ProgressType.Linear -> {
            if (progress != null) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = modifier,
                    color = indicatorColor
                )
            } else {
                LinearProgressIndicator(
                    modifier = modifier,
                    color = indicatorColor
                )
            }
        }
    }
}

// ==================== PREVIEWS ====================

@Preview(name = "Circular - Indeterminate", showBackground = true)
@Composable
private fun AppProgressCircularIndeterminatePreview() {
    KanzanTheme {
        AppProgressIndicator(
            type = ProgressType.Circular
        )
    }
}

@Preview(name = "Circular - Determinate", showBackground = true)
@Composable
private fun AppProgressCircularDeterminatePreview() {
    KanzanTheme {
        AppProgressIndicator(
            type = ProgressType.Circular,
            progress = 0.75f
        )
    }
}

@Preview(name = "Linear - Indeterminate", showBackground = true)
@Composable
private fun AppProgressLinearIndeterminatePreview() {
    KanzanTheme {
        AppProgressIndicator(
            type = ProgressType.Linear
        )
    }
}

@Preview(name = "Linear - Determinate", showBackground = true)
@Composable
private fun AppProgressLinearDeterminatePreview() {
    KanzanTheme {
        AppProgressIndicator(
            type = ProgressType.Linear,
            progress = 0.5f
        )
    }
}
