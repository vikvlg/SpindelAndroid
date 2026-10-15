package ru.vik.spindel.feature.brewing

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text

@Composable
fun BrewListScreen(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name from brewing!",
        modifier = modifier
    )
}
