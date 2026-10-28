package ru.vik.spindel.feature.brewing

import ru.vik.spindel.core.brews.api.Brew
import java.time.LocalDate

internal object BrewPreviewData {
    val brew = Brew(
        id = 1,
        name = "Пшеничное нефильтрованное",
        date = LocalDate.of(2026, 9, 1)
    )

    val brewList = listOf(brew)
}