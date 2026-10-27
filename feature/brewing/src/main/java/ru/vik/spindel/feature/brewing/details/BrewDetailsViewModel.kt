package ru.vik.spindel.feature.brewing.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vik.spindel.core.brews.api.BrewRepository
import ru.vik.spindel.feature.brewing.BrewingRoutes

@HiltViewModel
class BrewDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val brewRepository: BrewRepository
) : ViewModel() {
    private val route: BrewingRoutes.Details = savedStateHandle.toRoute<BrewingRoutes.Details>()
    val brewId = route.batchId
    private val _uiState = MutableStateFlow(BrewDetailsState())
    val uiState: StateFlow<BrewDetailsState> = _uiState.asStateFlow()

    init {
        handleIntent(BrewDetailsIntent.LoadDetails)
    }

    fun handleIntent(intent: BrewDetailsIntent) {
        when (intent) {
            is BrewDetailsIntent.LoadDetails -> loadBrew()
        }
    }

    private fun loadBrew() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            brewRepository.getBrew(brewId).onSuccess { brew ->
                _uiState.update { it.copy(isLoading = false, brew = brew) }
            }.onFailure { ex ->
                _uiState.update { it.copy(isLoading = false, brew = null) }
            }
        }
    }
}
