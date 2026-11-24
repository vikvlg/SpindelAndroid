package ru.vik.spindel.core.database.impl.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "devices",
    foreignKeys = [ForeignKey(BrewDto::class, ["id"], ["brew_id"])],
    indices = [Index(value = ["brew_id"])]
)
data class DeviceDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    @ColumnInfo(name = "brew_id")
    val brewId: Long? = null
)
