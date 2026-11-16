package ru.vik.spindel.feature.rawdata

import ru.vik.spindel.core.brews.api.Brew
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun Brew.toBrewUi(metricsCount: Int): BrewUi {
    return BrewUi(
        id = id,
        name = name,
        date = date,
        dateText = date.toFormattedDateString(),
        isActive = isActive,
        metricsCount = metricsCount,
    )
}

private fun LocalDateTime.toFormattedDateString(
    dateStyle: FormatStyle = FormatStyle.MEDIUM,
): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(dateStyle)
    return this.format(formatter)
}