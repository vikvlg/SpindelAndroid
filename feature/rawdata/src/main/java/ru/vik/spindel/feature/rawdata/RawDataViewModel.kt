package ru.vik.spindel.feature.rawdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vik.spindel.core.metrics.api.MetricsRepository

@HiltViewModel
class RawDataViewModel @Inject constructor(
    private val repository: MetricsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RawDataState())
    val uiState = _uiState.asStateFlow()

    init {
        // DEBUG TODO: Тестовое получение каких-то данных.
        viewModelScope.launch {
            repository.observeMetrics(1).collect { list ->
                _uiState.update { RawDataState(list = list) }
            }
        }
    }

    /** Обработчик действий пользователя. **/
    fun handleIntent(intent: RawDataIntent) {
        when (intent) {
            is RawDataIntent.ChangeSelected -> onChangeSelected(intent.id)
        }
    }

    /** Изменение выделения элемента таблицы. **/
    private fun onChangeSelected(id: Long) {
        _uiState.update { value ->
            value.copy(selected = value.selected.toggled(id))
        }
    }
}

fun <T> Set<T>.toggled(element: T): Set<T> =
    if (element in this) this - element else this + element

