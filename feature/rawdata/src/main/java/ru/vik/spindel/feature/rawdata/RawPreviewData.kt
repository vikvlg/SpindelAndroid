package ru.vik.spindel.feature.rawdata

import ru.vik.spindel.core.metrics.api.Metric
import java.time.LocalDateTime

internal object RawPreviewData {
    var metricId: Long = 0

    val metricList = listOf(
        Metric(++metricId, LocalDateTime.of(2026, 9, 1, 14, 0, 0), 16.0, 25.0),
        Metric(++metricId, LocalDateTime.of(2026, 9, 1, 15, 1, 0), 15.9, 24.0),
        Metric(++metricId, LocalDateTime.of(2026, 9, 1, 15, 59, 0), 15.8, 23.0),
        Metric(++metricId, LocalDateTime.of(2026, 9, 1, 17, 3, 0), 15.9, 22.0),
        Metric(++metricId, LocalDateTime.of(2026, 9, 1, 18, 0, 0), 15.8, 22.0),
        Metric(++metricId, LocalDateTime.of(2026, 9, 1, 18, 57, 0), 15.6, 22.5),
        Metric(++metricId, LocalDateTime.of(2026, 9, 1, 20, 1, 0), 15.3, 21.7),
    )
}