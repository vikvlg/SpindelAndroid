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
        _uiState.update { value ->
            value.copy(selection = selection)
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
            value.copy(selected = value.selected.toggled(id))
        }
    }

    /** Очистка списка метрик. **/
    private fun clearBrewMetrics() {
        _uiState.update { value ->
            value.copy(list = emptyList(), selectedBrew = null)
        }
    }

    /** Загрузка списка метрик варки. **/
    private fun loadBrewMetrics(brew: BrewUi?) {
        viewModelScope.launch {
            metricsRepository.observeMetrics(brew?.id)
                .onStart {
                    _uiState.update { value ->
                        value.copy(list = emptyList(), selectedBrew = brew)
                    }
                }
                .collect { list ->
                    _uiState.update { value ->
                        value.copy(list = list)
                    }
                }
        }
    }

    /** Загрузка списка варок. **/
    private fun loadBrewList() {
        viewModelScope.launch {
            brewRepository.observeBrews()
                .combine(metricsRepository.observeCountsByBrew()) { brews, brewMetrics ->
                    val unassignedCount = brewMetrics.firstOrNull { it.brewId == null }?.count
                    if (unassignedCount != null) {
                        _uiState.update { value ->
                            value.copy(unassignedCount = unassignedCount)
                        }
                    }

                    brews.map { brew ->
                        val count = brewMetrics.firstOrNull { it.brewId == brew.id}?.count ?: 0
                        brew.toBrewUi(count)
                    }
                }
                .collect { list ->
                //Log.d("TEST", "loadBrewList.list.size: ${list.size}")
                _uiState.update { value ->
                    value.copy(brews = list)
                }
            }
        }
    }
}

fun <T> Set<T>.toggled(element: T): Set<T> =
    if (element in this) this - element else this + element

