package ru.vik.spindel.feature.brewing.list

/** Эффекты при работе со списком варок. **/
sealed interface BrewListEffect {
    /** Отображение ошибки обновления данных. **/
    data class ShowError(val message: String?) : BrewListEffect
}