package ru.vik.spindel.feature.brewing.details

sealed interface BrewDetailsIntent {
    object LoadDetails : BrewDetailsIntent
}