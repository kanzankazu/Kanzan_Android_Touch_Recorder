package com.kanzan.feature.macro.organisms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzan.core.designsystem.theme.KanzanTheme
import com.kanzan.core.domain.model.Macro
import java.text.SimpleDateFormat
import java.util.*

/**
 * Organism: Macro card displaying macro info with actions
 */
@Composable
fun MacroCard(
    macro: Macro,
    onPlayClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPlayClick(macro.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Macro info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = macro.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (macro.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = macro.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${macro.events.size} events",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${macro.duration / 1000}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = formatDate(macro.updatedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { onPlayClick(macro.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play macro",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(
                    onClick = { onDeleteClick(macro.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete macro",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// Previews
@Preview(name = "Macro Card", showBackground = true)
@Composable
private fun MacroCardPreview() {
    KanzanTheme {
        MacroCard(
            macro = Macro(
                id = "1",
                name = "Swipe Up Gesture",
                description = "Quick swipe from bottom to top",
                events = emptyList(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                duration = 350
            ),
            onPlayClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(name = "Macro Card - No Description", showBackground = true)
@Composable
private fun MacroCardNoDescPreview() {
    KanzanTheme {
        MacroCard(
            macro = Macro(
                id = "2",
                name = "Long Press",
                description = "",
                events = emptyList(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                duration = 1000
            ),
            onPlayClick = {},
            onDeleteClick = {}
        )
    }
}
