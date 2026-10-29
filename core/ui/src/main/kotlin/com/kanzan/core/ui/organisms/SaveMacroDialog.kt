package com.kanzan.core.ui.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzan.core.designsystem.theme.KanzanTheme

/**
 * Dialog for saving recorded macro with name and description
 */
@Composable
fun SaveMacroDialog(
    eventCount: Int,
    duration: Long,
    onSave: (name: String, description: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Save Macro")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Recording info
                Text(
                    text = "Recorded: $eventCount events, ${duration / 1000}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = if (it.isBlank()) {
                            "Macro name is required"
                        } else null
                    },
                    label = { Text("Macro Name") },
                    placeholder = { Text("e.g., Login Flow") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("e.g., Auto-login sequence") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim(), description.trim())
                    } else {
                        nameError = "Macro name is required"
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Discard")
            }
        },
        modifier = modifier
    )
}

// ===========================================
// Previews
// ===========================================

@Preview(name = "Save Macro Dialog - Empty")
@Composable
private fun PreviewSaveMacroDialogEmpty() {
    KanzanTheme {
        SaveMacroDialog(
            eventCount = 42,
            duration = 15300,
            onSave = { _, _ -> },
            onDismiss = {}
        )
    }
}

@Preview(name = "Save Macro Dialog - Filled")
@Composable
private fun PreviewSaveMacroDialogFilled() {
    KanzanTheme {
        var showDialog by remember { mutableStateOf(true) }
        
        if (showDialog) {
            SaveMacroDialog(
                eventCount = 123,
                duration = 28500,
                onSave = { _, _ -> showDialog = false },
                onDismiss = { showDialog = false }
            )
        }
    }
}
