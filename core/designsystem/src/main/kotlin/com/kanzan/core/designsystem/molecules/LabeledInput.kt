package com.kanzan.core.designsystem.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzan.core.designsystem.atoms.AppTextField
import com.kanzan.core.designsystem.theme.KanzanTheme

/**
 * Labeled input molecule combining label, text field, and error text
 *
 * Provides complete input field dengan label dan error handling
 *
 * @param value Current input value
 * @param onValueChange Callback when value changes
 * @param label Field label (shown above)
 * @param modifier Modifier for customization
 * @param placeholder Placeholder text
 * @param error Error message (null if no error)
 * @param enabled Whether field is enabled
 * @param supportingText Helper text below field
 */
@Composable
fun LabeledInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    supportingText: String? = null
) {
    Column(modifier = modifier) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (error != null) MaterialTheme.colorScheme.error 
                   else MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Text field
        AppTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            enabled = enabled,
            isError = error != null,
            supportingText = error ?: supportingText,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ==================== PREVIEWS ====================

@Preview(name = "Empty", showBackground = true)
@Composable
private fun LabeledInputEmptyPreview() {
    KanzanTheme {
        LabeledInput(
            value = "",
            onValueChange = {},
            label = "Macro Name",
            placeholder = "Enter macro name"
        )
    }
}

@Preview(name = "With Value", showBackground = true)
@Composable
private fun LabeledInputWithValuePreview() {
    KanzanTheme {
        LabeledInput(
            value = "My Test Macro",
            onValueChange = {},
            label = "Macro Name",
            supportingText = "Give your macro a descriptive name"
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun LabeledInputErrorPreview() {
    KanzanTheme {
        LabeledInput(
            value = "",
            onValueChange = {},
            label = "Macro Name",
            error = "Name cannot be empty"
        )
    }
}

@Preview(name = "Disabled", showBackground = true)
@Composable
private fun LabeledInputDisabledPreview() {
    KanzanTheme {
        LabeledInput(
            value = "My Test Macro",
            onValueChange = {},
            label = "Macro Name",
            enabled = false
        )
    }
}
