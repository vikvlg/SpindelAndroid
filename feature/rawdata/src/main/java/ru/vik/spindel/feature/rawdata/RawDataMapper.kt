package ru.vik.spindel.feature.rawdata

import ru.vik.spindel.core.brews.api.Brew

fun Brew.toBrewUi(metricsCount: Int): BrewUi {
    return BrewUi(
        id = id,
        name = name,
        date = date,
        dateText = date.toString(),
        isActive = true,
        metricsCount = metricsCount,
    )
}
