package com.kanzan.core.designsystem.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzan.core.designsystem.theme.KanzanTheme

/**
 * Interval slider molecule for time selection
 *
 * Slider dengan label dan value display, designed untuk auto-click interval
 *
 * @param value Current interval value in milliseconds
 * @param onValueChange Callback when value changes
 * @param modifier Modifier for customization
 * @param label Slider label
 * @param valueRange Min-Max range untuk slider
 * @param steps Number of discrete steps (0 for continuous)
 * @param enabled Whether slider is enabled
 */
@Composable
fun IntervalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Interval",
    valueRange: ClosedFloatingPointRange<Float> = 100f..5000f,
    steps: Int = 49,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        // Header with label and value
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = "${value.toInt()} ms",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Slider
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ==================== PREVIEWS ====================

@Preview(name = "Min Value", showBackground = true)
@Composable
private fun IntervalSliderMinPreview() {
    KanzanTheme {
        IntervalSlider(
            value = 100f,
            onValueChange = {},
            label = "Click Interval"
        )
    }
}

@Preview(name = "Mid Value", showBackground = true)
@Composable
private fun IntervalSliderMidPreview() {
    KanzanTheme {
        IntervalSlider(
            value = 1000f,
            onValueChange = {},
            label = "Click Interval"
        )
    }
}

@Preview(name = "Max Value", showBackground = true)
@Composable
private fun IntervalSliderMaxPreview() {
    KanzanTheme {
        IntervalSlider(
            value = 5000f,
            onValueChange = {},
            label = "Click Interval"
        )
    }
}
