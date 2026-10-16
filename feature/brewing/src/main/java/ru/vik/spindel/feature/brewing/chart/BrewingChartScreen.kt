package ru.vik.spindel.feature.brewing.chart

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BrewingChartScreen(batchId: Long) {
    Column {
        Text(
            text = "Chart for batch $batchId"
        )
    }
}
