package ru.vik.spindel.feature.rawdata

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class RawDataViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow(RawDataState())
    val uiState = _uiState.asStateFlow()
}