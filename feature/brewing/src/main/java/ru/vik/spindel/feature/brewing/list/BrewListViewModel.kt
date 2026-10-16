package ru.vik.spindel.feature.brewing.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.brews.api.BrewRepository

@HiltViewModel
class BrewListViewModel @Inject constructor(
    private val repository: BrewRepository
) : ViewModel() {

    val brewList: StateFlow<List<Brew>> = repository.observeBrews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Канал для одноразовых событий (ошибок)
    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    fun loadData() {
        viewModelScope.launch {
            repository.refreshBrews().onFailure { error ->
                // Сеть упала, но на экране останутся старые данные из БД,
                // а пользователю покажем всплывающее уведомление
                _errorEvent.emit("Не удалось обновить данные: ${error.message}")
            }
        }
    }
}