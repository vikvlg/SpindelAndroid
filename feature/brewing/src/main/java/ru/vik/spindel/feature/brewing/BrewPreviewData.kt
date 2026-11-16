package ru.vik.spindel.feature.brewing

import ru.vik.spindel.core.brews.api.Brew
import java.time.LocalDateTime

internal object BrewPreviewData {
    val brew = Brew(
        id = 1,
        name = "Пшеничное нефильтрованное",
        date = LocalDateTime.of(2026, 9, 1, 18, 0, 0),
        isActive = true,
    )

    val brewList = listOf(brew)
}