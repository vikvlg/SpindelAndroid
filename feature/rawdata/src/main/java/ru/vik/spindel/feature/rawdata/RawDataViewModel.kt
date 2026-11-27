package ru.vik.spindel.feature.rawdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vik.spindel.core.brews.api.BrewRepository
import ru.vik.spindel.core.metrics.api.MetricsRepository

@HiltViewModel
class RawDataViewModel @Inject constructor(
    private val brewRepository: BrewRepository,
    private val metricsRepository: MetricsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RawDataState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBrewList()
    }

    /** Обработчик действий пользователя. **/
    fun handleIntent(intent: RawDataIntent) {
        when (intent) {
            is RawDataIntent.ChangeRowSelected -> onRowChangeSelected(intent.id)
            is RawDataIntent.SelectBrew -> selectBrew(intent.selection)
        }
    }

    /** Выбор варки для отображения метрик. **/
    private fun selectBrew(selection: BrewSelection) {
        val brewSelector = selection.toSelectorState()
        _uiState.update { value ->
            value.copy(
                brewPicker = value.brewPicker.copy(selection = selection),
                brewSelector = brewSelector
            )
        }
        when (selection) {
            is BrewSelection.Brew -> loadBrewMetrics(selection.brew)
            is BrewSelection.NotSelected -> clearBrewMetrics()
            is BrewSelection.Unassigned -> loadBrewMetrics(null)
        }
    }

    /** Изменение выделения элемента таблицы. **/
    private fun onRowChangeSelected(id: Long) {
        _uiState.update { value ->
            value.copy(metrics = value.metrics.copy(selected = value.metrics.selected.toggled(id)))
        }
    }

    /** Очистка списка метрик. **/
    private fun clearBrewMetrics() {
        _uiState.update { value ->
            value.copy(
                metrics = MetricsState(),
                brewPicker = value.brewPicker.copy(selectedBrew = null)
            )
        }
    }

    /** Загрузка списка метрик варки. **/
    private fun loadBrewMetrics(brew: BrewUi?) {
        viewModelScope.launch {
            metricsRepository.observeMetrics(brew?.id)
                .onStart {
                    _uiState.update { value ->
                        value.copy(
                            metrics = MetricsState().copy(isLoading = true),
                            brewPicker = value.brewPicker.copy(selectedBrew = brew)
                        )
                    }
                    metricsRepository.refreshMetrics(brew?.id)
                }
                .collect { list ->
                    val metricsUi = list.map { it.toMetricUi() }

                    _uiState.update { value ->
                        value.copy(
                            metrics = value.metrics.copy(list = metricsUi, isLoading = false)
                        )
                    }
                }
        }
    }

    /** Загрузка списка варок. **/
    private fun loadBrewList() {
        viewModelScope.launch {
            brewRepository.observeBrews()
                // THINKING [2026.11.16]: Есть ли смысл вынести часть этого кода в UseCase?
                .combine(metricsRepository.observeCountsByBrew()) { brews, brewMetrics ->
                    val unassignedCount = brewMetrics.firstOrNull { it.brewId == null }?.count
                    if (unassignedCount != null) {
                        _uiState.update { value ->
                            value.copy(brewPicker = value.brewPicker.copy(unassignedCount = unassignedCount))
                        }
                    }
                    val counts = brewMetrics.associate { it.brewId to it.count }

                    brews.map { brew -> brew.toBrewUi(counts[brew.id] ?: 0) }
                }
                .collect { brewUiList ->
                    _uiState.update { it.copy(brewPicker = it.brewPicker.copy(brews = brewUiList)) }
                }
        }
    }
}

private fun <T> Set<T>.toggled(element: T): Set<T> =
    if (element in this) this - element else this + element

