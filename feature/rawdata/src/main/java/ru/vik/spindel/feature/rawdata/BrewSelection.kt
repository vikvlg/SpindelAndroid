package ru.vik.spindel.feature.rawdata

/** Выбор варки. **/
sealed interface BrewSelection {
    /** Варка не выбрана. **/
    data object NotSelected : BrewSelection

    /** Данные без привязки к какой-либо варке. **/
    data object Unassigned : BrewSelection

    /** Выбор конкретной варки. **/
    data class Brew(val brew: BrewUi) : BrewSelection
}
