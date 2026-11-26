package ru.vik.spindel.core.database.impl

import androidx.room.TypeConverter
import java.time.LocalDateTime

internal class Converters {
    companion object {
        // Формат даты соответствует ISO-8601 (DateTimeFormatter.ISO_LOCAL_DATE).
        private const val FULL_FORMAT_DATE = "yyyy-MM-dd HH:mm:ss"
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.toString()
    }
}