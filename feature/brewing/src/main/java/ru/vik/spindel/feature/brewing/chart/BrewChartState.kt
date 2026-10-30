package ru.vik.spindel.feature.brewing.chart

import ru.vik.spindel.core.metrics.api.Metric

/** Состояние экрана с графиком брожения. **/
data class BrewChartState(
    val isLoading: Boolean = false,
    val metrics: List<Metric> = emptyList()
)