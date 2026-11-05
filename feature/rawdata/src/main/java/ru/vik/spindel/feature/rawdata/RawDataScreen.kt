package ru.vik.spindel.feature.rawdata

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RawDataRoute(viewModel: RawDataViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    RawDataScreen(state)
}

@Composable
fun RawDataScreen(state: RawDataState) {
    Box {
        Text(
            text = "Это таблица на ${state.list.size} строк",
        )
    }
}
