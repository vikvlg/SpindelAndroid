package ru.vik.spindel.feature.rawdata

import java.time.LocalDateTime

/** UI-представление варки. **/
data class BrewUi(
    val id: Long,
    val name: String,
    val date: LocalDateTime,
    val dateText: String,
    val isActive: Boolean,
    val metricsCount: Int,
)
