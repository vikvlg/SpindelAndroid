package ru.vik.spindel.core.database.impl.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "brews")
data class BrewDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val date: LocalDateTime,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
)
