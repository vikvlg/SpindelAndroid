package ru.vik.spindel.core.database.impl.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "metrics",
    foreignKeys = [ForeignKey(entity = BrewDto::class, parentColumns = ["id"], childColumns = ["brew_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["brew_id"])]
)
data class MetricDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val date: LocalDateTime,
    @ColumnInfo(name = "brew_id")
    val brewId: Long,
    val gravity: Double,
    val temperature: Double,
)
