package com.kanzan.core.designsystem.atoms

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.kanzan.core.designsystem.theme.KanzanTheme

/**
 * Reusable text field component following Material 3 design
 *
 * Outlined text field dengan error state support
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param modifier Modifier for customization
 * @param label Field label
 * @param placeholder Placeholder text
 * @param enabled Whether field is enabled
 * @param isError Whether field shows error state
 * @param supportingText Helper or error text below field
 * @param singleLine Whether to limit to single line
 * @param keyboardType Type of keyboard to show
 * @param imeAction IME action button
 * @param keyboardActions Keyboard actions handler
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        enabled = enabled,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions
    )
}

// ==================== PREVIEWS ====================

@Preview(name = "Empty", showBackground = true)
@Composable
private fun AppTextFieldEmptyPreview() {
    KanzanTheme {
        AppTextField(
            value = "",
            onValueChange = {},
            label = "Email Address",
            placeholder = "Enter your email"
        )
    }
}

@Preview(name = "With Value", showBackground = true)
@Composable
private fun AppTextFieldWithValuePreview() {
    KanzanTheme {
        AppTextField(
            value = "user@example.com",
            onValueChange = {},
            label = "Email Address"
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun AppTextFieldErrorPreview() {
    KanzanTheme {
        AppTextField(
            value = "invalid",
            onValueChange = {},
            label = "Email Address",
            isError = true,
            supportingText = "Invalid email format"
        )
    }
}

@Preview(name = "Disabled", showBackground = true)
@Composable
private fun AppTextFieldDisabledPreview() {
    KanzanTheme {
        AppTextField(
            value = "user@example.com",
            onValueChange = {},
            label = "Email Address",
            enabled = false
        )
    }
}
