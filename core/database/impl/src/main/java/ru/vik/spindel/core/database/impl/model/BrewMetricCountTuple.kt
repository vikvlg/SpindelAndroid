package ru.vik.spindel.core.database.impl.model

import androidx.room.ColumnInfo

data class BrewMetricCountTuple(
    @ColumnInfo(name = "brew_id")
    val brewId: Long?,
    val count: Int
)
