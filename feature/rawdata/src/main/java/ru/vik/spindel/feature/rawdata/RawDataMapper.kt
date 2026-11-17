package ru.vik.spindel.feature.rawdata

import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.metrics.api.Metric
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.String

fun Brew.toBrewUi(metricsCount: Int): BrewUi {
    return BrewUi(
        id = id,
        name = name,
        date = date,
        dateText = date.toBrewDateText(),
        isActive = isActive,
        metricsCount = metricsCount,
    )
}

fun Metric.toMetricUi(): MetricUi {
    return MetricUi(
        id = id,
        dateText = date.toMetricDateText(),
        gravityText = gravity.formattedOneDecimal(),
        temperatureText = temperature.formattedOneDecimal(),
    )
}

private fun LocalDateTime.toBrewDateText(
    dateStyle: FormatStyle = FormatStyle.MEDIUM,
): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(dateStyle)
    return this.format(formatter)
}

private fun LocalDateTime.toMetricDateText(
    dateStyle: FormatStyle = FormatStyle.SHORT,
    timeStyle: FormatStyle = FormatStyle.SHORT,
): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle)
    return this.format(formatter)
}

private fun Double.formattedOneDecimal(): String {
    return String.format(Locale.getDefault(), "%.1f", this)
}
