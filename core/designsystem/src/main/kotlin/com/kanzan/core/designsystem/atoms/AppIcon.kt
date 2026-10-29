package com.kanzan.core.designsystem.atoms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzan.core.designsystem.theme.KanzanTheme

/**
 * Icon size presets
 */
enum class IconSize(val dp: Dp) {
    Small(16.dp),
    Medium(24.dp),
    Large(32.dp)
}

/**
 * Reusable icon component with size variants
 *
 * Wrapper untuk Material Icon dengan predefined sizes dan color support
 *
 * @param imageVector Icon vector drawable
 * @param contentDescription Accessibility description
 * @param modifier Modifier for customization
 * @param size Icon size preset
 * @param tint Icon color (null = uses LocalContentColor)
 */
@Composable
fun AppIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: IconSize = IconSize.Medium,
    tint: Color? = null
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint ?: LocalContentColor.current
    )
}

// ==================== PREVIEWS ====================

@Preview(name = "Small - Default", showBackground = true)
@Composable
private fun AppIconSmallDefaultPreview() {
    KanzanTheme {
        AppIcon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            size = IconSize.Small
        )
    }
}

@Preview(name = "Small - Disabled", showBackground = true)
@Composable
private fun AppIconSmallDisabledPreview() {
    KanzanTheme {
        AppIcon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            size = IconSize.Small,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

@Preview(name = "Medium - Default", showBackground = true)
@Composable
private fun AppIconMediumDefaultPreview() {
    KanzanTheme {
        AppIcon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            size = IconSize.Medium
        )
    }
}

@Preview(name = "Medium - Disabled", showBackground = true)
@Composable
private fun AppIconMediumDisabledPreview() {
    KanzanTheme {
        AppIcon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            size = IconSize.Medium,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

@Preview(name = "Large - Default", showBackground = true)
@Composable
private fun AppIconLargeDefaultPreview() {
    KanzanTheme {
        AppIcon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            size = IconSize.Large
        )
    }
}

@Preview(name = "Large - Disabled", showBackground = true)
@Composable
private fun AppIconLargeDisabledPreview() {
    KanzanTheme {
        AppIcon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            size = IconSize.Large,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}
