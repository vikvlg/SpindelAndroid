package ru.vik.spindel.feature.rawdata

import ru.vik.spindel.core.metrics.api.Metric

data class RawDataState(
    val list: List<Metric> = emptyList()
)