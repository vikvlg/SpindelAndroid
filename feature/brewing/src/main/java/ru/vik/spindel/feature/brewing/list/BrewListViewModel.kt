package ru.vik.spindel.feature.brewing.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.vik.spindel.core.brews.api.BrewRepository

@HiltViewModel
class BrewListViewModel @Inject constructor(
    private val repository: BrewRepository
) : ViewModel() {

    /** Флаг первой загрузки данных, чтобы ограничить повторяющиеся индикаторы. **/
    private var isFirstLoading = true
    /** Флаг-событие начала загрузки данных. **/
    private val _isLoading = MutableStateFlow(false)

    /** Единое UI-состояние экрана списка варок. **/
    val state: StateFlow<BrewListState> = repository.observeBrews()
        .onStart { emit(emptyList()) }
        .combine(_isLoading) { dbItems, loading ->
            BrewListState(loading, dbItems)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BrewListState()
        )

    private val _effect = MutableSharedFlow<BrewListEffect>()
    /** События списка варок. **/
    val effect = _effect.asSharedFlow()

    /** Обработчик действий пользователя. **/
    fun handleIntent(intent: BrewListIntent) {
        when (intent) {
            is BrewListIntent.RefreshClicked -> loadBrewList()
        }
    }

    /** Подгружает список варок. **/
    private fun loadBrewList() {
        viewModelScope.launch {
            _isLoading.value = isFirstLoading
            isFirstLoading = false

            repository.refreshBrews()
                .onSuccess {
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _isLoading.value = false
                    _effect.emit(BrewListEffect.ShowError("Не удалось обновить данные: ${error.message}"))
                }
        }
    }
}