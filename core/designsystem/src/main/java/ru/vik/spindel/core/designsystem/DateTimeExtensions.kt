package ru.vik.spindel.core.designsystem

import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Получает актуальную локаль системы прямо в момент вызова
 */
fun getCurrentLocale(): Locale {
    val configuration = Resources.getSystem().configuration
    return ConfigurationCompat.getLocales(configuration)[0] ?: Locale.getDefault()
}

/**
 * Преобразует LocalDateTime в красивую строку для UI с учетом текущей локали
 */
fun LocalDateTime.toUiString(): String {
    // Форматтер создается/извлекается с актуальной локалью в момент вызова
    val formatter = DateTimeFormatter
        .ofPattern("dd.MM HH:mm")
        .withLocale(getCurrentLocale())

    return this.format(formatter)
}

fun LocalDate.toUiString(): String {
    // Форматтер создается/извлекается с актуальной локалью в момент вызова
    val formatter = DateTimeFormatter
        .ofPattern("dd.MM")
        .withLocale(getCurrentLocale())

    return this.format(formatter)
}
