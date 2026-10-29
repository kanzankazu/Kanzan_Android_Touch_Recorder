package com.kanzan.core.designsystem.atoms

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kanzan.core.designsystem.theme.KanzanTheme

/**
 * Reusable switch component following Material 3 design
 *
 * Toggle switch untuk on/off states
 *
 * @param checked Whether switch is on
 * @param onCheckedChange Callback when switch is toggled
 * @param modifier Modifier for customization
 * @param enabled Whether switch is enabled
 */
@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled
    )
}

// ==================== PREVIEWS ====================

@Preview(name = "On", showBackground = true)
@Composable
private fun AppSwitchOnPreview() {
    KanzanTheme {
        AppSwitch(
            checked = true,
            onCheckedChange = {}
        )
    }
}

@Preview(name = "Off", showBackground = true)
@Composable
private fun AppSwitchOffPreview() {
    KanzanTheme {
        AppSwitch(
            checked = false,
            onCheckedChange = {}
        )
    }
}

@Preview(name = "Disabled", showBackground = true)
@Composable
private fun AppSwitchDisabledPreview() {
    KanzanTheme {
        AppSwitch(
            checked = true,
            onCheckedChange = {},
            enabled = false
        )
    }
}
