package ru.vik.spindel.core.brews.api

import java.time.LocalDate

data class Brew(
    val id: Long,
    val name: String,
    val date: LocalDate,
)