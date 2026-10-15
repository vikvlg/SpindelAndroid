package ru.vik.spindel.feature.brewing.list

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text

@Composable
fun BrewListScreen(onSubItemClick: (batchId: Int) -> Unit) {
    Column {
        Text(
            text = "This is LIST!!! in future"
        )
        Button(
            onClick = { onSubItemClick(0) }
        ) {
            Text(
                text = "Details"
            )
        }
    }
}
