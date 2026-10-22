package ru.vik.spindel.feature.brewing.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import ru.vik.spindel.core.metrics.api.MetricsRepository

@HiltViewModel
class BrewChartViewModel @Inject constructor(
    private val repository: MetricsRepository
) : ViewModel() {
    val brewId: Long = 0
    val metrics = repository.observeMetrics(brewId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}