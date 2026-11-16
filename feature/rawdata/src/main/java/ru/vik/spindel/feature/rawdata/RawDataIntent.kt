package ru.vik.spindel.feature.rawdata

sealed interface RawDataIntent {
    data class ChangeRowSelected(val id: Long) : RawDataIntent
    data class SelectBrew(val selection: BrewSelection) : RawDataIntent
}
