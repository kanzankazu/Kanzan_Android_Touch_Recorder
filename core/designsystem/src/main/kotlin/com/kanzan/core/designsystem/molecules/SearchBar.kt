package com.kanzan.core.designsystem.molecules

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kanzan.core.designsystem.theme.KanzanTheme

/**
 * Search bar molecule combining TextField with search icon
 *
 * Provides search functionality dengan clear button
 *
 * @param query Current search query
 * @param onQueryChange Callback when query changes
 * @param modifier Modifier for customization
 * @param placeholder Placeholder text
 * @param onClear Callback when clear button clicked
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    onClear: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = if (query.isNotEmpty() && onClear != null) {
            {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear"
                    )
                }
            }
        } else null,
        singleLine = true
    )
}

// ==================== PREVIEWS ====================

@Preview(name = "Empty", showBackground = true)
@Composable
private fun SearchBarEmptyPreview() {
    KanzanTheme {
        SearchBar(
            query = "",
            onQueryChange = {},
            placeholder = "Search macros..."
        )
    }
}

@Preview(name = "With Text", showBackground = true)
@Composable
private fun SearchBarWithTextPreview() {
    KanzanTheme {
        SearchBar(
            query = "test macro",
            onQueryChange = {},
            placeholder = "Search macros...",
            onClear = {}
        )
    }
}

@Preview(name = "Focused", showBackground = true)
@Composable
private fun SearchBarFocusedPreview() {
    KanzanTheme {
        SearchBar(
            query = "a",
            onQueryChange = {},
            placeholder = "Search macros...",
            onClear = {}
        )
    }
}
