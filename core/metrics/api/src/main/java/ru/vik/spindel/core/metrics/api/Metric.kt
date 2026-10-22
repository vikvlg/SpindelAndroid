package ru.vik.spindel.core.metrics.api

import java.time.LocalDateTime

data class Metric(
    val id: Long,
    val date: LocalDateTime,
    val gravity: Double,
    val temperature: Double,
)