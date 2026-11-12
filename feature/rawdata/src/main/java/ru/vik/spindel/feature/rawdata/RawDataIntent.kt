package ru.vik.spindel.feature.rawdata

sealed interface RawDataIntent {
    data class ChangeSelected(val id: Long) : RawDataIntent
}
