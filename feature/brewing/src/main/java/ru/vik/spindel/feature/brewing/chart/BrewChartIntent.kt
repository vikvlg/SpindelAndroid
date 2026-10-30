package ru.vik.spindel.feature.brewing.chart

/** Действия пользователя на экране с графиком брожения. **/
sealed interface BrewChartIntent {
    data class RefreshChart(val brewId: Long) : BrewChartIntent
}