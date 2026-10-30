package ru.vik.spindel.feature.brewing.chart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import ru.vik.spindel.core.metrics.api.MetricsRepository
import ru.vik.spindel.feature.brewing.BrewingRoutes

@HiltViewModel
class BrewChartViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: MetricsRepository
) : ViewModel() {
    private val route: BrewingRoutes.Chart = savedStateHandle.toRoute<BrewingRoutes.Chart>()
    val brewId: Long = route.batchId

    /** UI-состояние экрана графика брожения. **/
    val uiState = repository.observeMetrics(brewId)
        .map { metrics ->
            BrewChartState(false, metrics)
        }
        .onStart {
            emit(BrewChartState(true))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BrewChartState(isLoading = true)
        )

    /** Обработчик действий пользователя. **/
    fun handleIntent(intent: BrewChartIntent) {
    }
}