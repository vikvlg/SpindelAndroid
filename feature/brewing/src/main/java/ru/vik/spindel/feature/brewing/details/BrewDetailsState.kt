package ru.vik.spindel.feature.brewing.details

import ru.vik.spindel.core.brews.api.Brew

data class BrewDetailsState(
    val isLoading: Boolean = false,
    val brew: Brew? = null,
    val details: BrewDetailsData? = null
)
