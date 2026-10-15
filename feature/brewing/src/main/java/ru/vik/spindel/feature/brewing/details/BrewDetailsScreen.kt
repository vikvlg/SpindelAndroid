package ru.vik.spindel.feature.brewing.details

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BrewDetailsScreen(batchId: Int, onShowChart: () -> Unit) {
    Column {
        Text(
            text = "Details for batch $batchId"
        )
        Button(
            onClick = onShowChart
        ) {
            Text(
                text = "График"
            )
        }
    }
}
