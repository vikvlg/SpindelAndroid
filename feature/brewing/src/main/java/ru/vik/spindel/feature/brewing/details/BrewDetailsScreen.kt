package ru.vik.spindel.feature.brewing.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.vik.spindel.core.designsystem.theme.SpindelTheme
import ru.vik.spindel.feature.brewing.BrewPreviewData
import ru.vik.spindel.feature.brewing.R

@Composable
fun BrewDetailsRoute(
    onChartClick: (brewId: Long) -> Unit,
    viewModel: BrewDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BrewDetailsScreen(state = state, onChartClick, onIntent = viewModel::handleIntent)
}

@Composable
fun BrewDetailsScreen(
    state: BrewDetailsState,
    onChartClick: (brewId: Long) -> Unit,
    onIntent: (BrewDetailsIntent) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column {
                val brew = state.brew
                val dataDetails = state.details
                if (brew == null) {
                    Text(
                        text = stringResource(R.string.brewing_details_brew_not_found)
                    )
                } else {
                    KeyValueRow(
                        key = stringResource(R.string.brewing_details_brew_name),
                        value = brew.name
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    KeyValueRow(
                        key = stringResource(R.string.brewing_details_brew_date_time),
                        value = brew.date.toString()
                    )

                    if (dataDetails == null) {
                        Text(
                            text = stringResource(R.string.brewing_details_brew_data_empty)
                        )
                    }

                    Button(
                        onClick = { onChartClick(brew.id) },
                    ) {
                        Text(text = stringResource(R.string.brewing_details_on_chart))
                    }
                } // if brew
            }
        }
    }
}

@Composable
fun KeyValueRow(key: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = key,
            // Используем вторичный текст (он автоматически станет светло-серым в светлой теме и нежно-серым в темной)
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            // Основной контрастный текст для значений
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun BrewDetailsLoadingPreview() {
    SpindelTheme {
        BrewDetailsScreen(
            state = BrewDetailsState(isLoading = true),
            onChartClick = {},
            onIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BrewDetailsContentPreview() {
    SpindelTheme {
        BrewDetailsScreen(
            state = BrewDetailsState(isLoading = false, brew = BrewPreviewData.brew),
            onChartClick = {},
            onIntent = {}
        )
    }
}
