package ru.vik.spindel.feature.devices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.vik.spindel.core.devices.api.Device

@Composable
fun DeviceListScreen(
    onSubItemClick: (batchId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeviceViewModel = hiltViewModel()
) {
    val devices by viewModel.devices.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        if (devices.isEmpty()) {
            Text(
                text = stringResource(R.string.devices_list_empty),
                modifier = modifier
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = devices,
                key = { device -> device.id }
            ) { device ->
                DeviceItemRow(device, onSubItemClick)
            }
        }
    }
}

@Composable
fun DeviceItemRow(
    device: Device,
    onItemClick: (batchId: Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(device.id) },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = device.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}