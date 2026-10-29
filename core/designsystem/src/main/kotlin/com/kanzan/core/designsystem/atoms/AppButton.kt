package com.kanzan.core.designsystem.atoms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzan.core.designsystem.theme.KanzanTheme

/**
 * App button variants
 */
enum class ButtonVariant {
    Primary,
    Secondary,
    Text
}

/**
 * Reusable button component following Material 3 design
 *
 * Supports 3 variants (Primary, Secondary, Text) dengan loading state
 *
 * @param text Button label
 * @param onClick Click handler
 * @param modifier Modifier for customization
 * @param enabled Whether button is enabled
 * @param isLoading Whether button shows loading indicator
 * @param variant Button style variant
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Primary
) {
    val buttonEnabled = enabled && !isLoading
    
    when (variant) {
        ButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                enabled = buttonEnabled,
                modifier = modifier,
                contentPadding = if (isLoading) PaddingValues(horizontal = 24.dp, vertical = 12.dp) else ButtonDefaults.ContentPadding
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text)
                }
            }
        }
        
        ButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                enabled = buttonEnabled,
                modifier = modifier,
                contentPadding = if (isLoading) PaddingValues(horizontal = 24.dp, vertical = 12.dp) else ButtonDefaults.ContentPadding
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text)
                }
            }
        }
        
        ButtonVariant.Text -> {
            TextButton(
                onClick = onClick,
                enabled = buttonEnabled,
                modifier = modifier,
                contentPadding = if (isLoading) PaddingValues(horizontal = 16.dp, vertical = 8.dp) else ButtonDefaults.TextButtonContentPadding
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text)
                }
            }
        }
    }
}

// ==================== PREVIEWS ====================

@Preview(name = "Primary - Enabled", showBackground = true)
@Composable
private fun AppButtonPrimaryEnabledPreview() {
    KanzanTheme {
        AppButton(
            text = "Click Me",
            onClick = {},
            variant = ButtonVariant.Primary
        )
    }
}

@Preview(name = "Primary - Disabled", showBackground = true)
@Composable
private fun AppButtonPrimaryDisabledPreview() {
    KanzanTheme {
        AppButton(
            text = "Click Me",
            onClick = {},
            enabled = false,
            variant = ButtonVariant.Primary
        )
    }
}

@Preview(name = "Primary - Loading", showBackground = true)
@Composable
private fun AppButtonPrimaryLoadingPreview() {
    KanzanTheme {
        AppButton(
            text = "Loading...",
            onClick = {},
            isLoading = true,
            variant = ButtonVariant.Primary
        )
    }
}

@Preview(name = "Secondary - Enabled", showBackground = true)
@Composable
private fun AppButtonSecondaryEnabledPreview() {
    KanzanTheme {
        AppButton(
            text = "Click Me",
            onClick = {},
            variant = ButtonVariant.Secondary
        )
    }
}

@Preview(name = "Secondary - Disabled", showBackground = true)
@Composable
private fun AppButtonSecondaryDisabledPreview() {
    KanzanTheme {
        AppButton(
            text = "Click Me",
            onClick = {},
            enabled = false,
            variant = ButtonVariant.Secondary
        )
    }
}

@Preview(name = "Secondary - Loading", showBackground = true)
@Composable
private fun AppButtonSecondaryLoadingPreview() {
    KanzanTheme {
        AppButton(
            text = "Loading...",
            onClick = {},
            isLoading = true,
            variant = ButtonVariant.Secondary
        )
    }
}

@Preview(name = "Text - Enabled", showBackground = true)
@Composable
private fun AppButtonTextEnabledPreview() {
    KanzanTheme {
        AppButton(
            text = "Click Me",
            onClick = {},
            variant = ButtonVariant.Text
        )
    }
}

@Preview(name = "Text - Disabled", showBackground = true)
@Composable
private fun AppButtonTextDisabledPreview() {
    KanzanTheme {
        AppButton(
            text = "Click Me",
            onClick = {},
            enabled = false,
            variant = ButtonVariant.Text
        )
    }
}

@Preview(name = "Text - Loading", showBackground = true)
@Composable
private fun AppButtonTextLoadingPreview() {
    KanzanTheme {
        AppButton(
            text = "Loading...",
            onClick = {},
            isLoading = true,
            variant = ButtonVariant.Text
        )
    }
}
