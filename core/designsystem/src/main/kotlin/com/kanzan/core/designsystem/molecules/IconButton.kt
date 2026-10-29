package com.kanzan.core.designsystem.molecules

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzan.core.designsystem.theme.KanzanTheme

/**
 * Icon button molecule with loading state support
 *
 * Filled icon button yang bisa show loading indicator
 *
 * @param icon Icon vector to display
 * @param contentDescription Accessibility description
 * @param onClick Click handler
 * @param modifier Modifier for customization
 * @param enabled Whether button is enabled
 * @param isLoading Whether to show loading indicator
 */
@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = IconButtonDefaults.filledIconButtonColors()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription
            )
        }
    }
}

// ==================== PREVIEWS ====================

@Preview(name = "Enabled", showBackground = true)
@Composable
private fun AppIconButtonEnabledPreview() {
    KanzanTheme {
        AppIconButton(
            icon = Icons.Default.PlayArrow,
            contentDescription = "Play",
            onClick = {}
        )
    }
}

@Preview(name = "Disabled", showBackground = true)
@Composable
private fun AppIconButtonDisabledPreview() {
    KanzanTheme {
        AppIconButton(
            icon = Icons.Default.PlayArrow,
            contentDescription = "Play",
            onClick = {},
            enabled = false
        )
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun AppIconButtonLoadingPreview() {
    KanzanTheme {
        AppIconButton(
            icon = Icons.Default.PlayArrow,
            contentDescription = "Play",
            onClick = {},
            isLoading = true
        )
    }
}
