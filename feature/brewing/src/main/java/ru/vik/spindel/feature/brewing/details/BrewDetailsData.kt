package ru.vik.spindel.feature.brewing.details

data class BrewDetailsData(
    val minGravity: Float,
    val maxGravity: Float,
    val minTemperature: Float,
    val maxTemperature: Float,
    /** Продолжительность брожения, сутки. **/
    val fermentation: Int,
)
