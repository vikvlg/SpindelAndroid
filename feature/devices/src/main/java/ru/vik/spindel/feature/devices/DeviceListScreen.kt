package ru.vik.spindel.feature.devices

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DeviceListScreen(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name from devices!",
        modifier = modifier
    )
}
