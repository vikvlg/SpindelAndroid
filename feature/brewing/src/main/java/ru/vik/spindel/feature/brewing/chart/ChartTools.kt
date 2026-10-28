package ru.vik.spindel.feature.brewing.chart

import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToLong

internal const val SECONDS_PER_DAY = 86_400.0

/**
 * Значение по оси X: количество суток от 01.01.1970, целое число соответствует полуночи.
 * ZoneOffset.UTC здесь не перевод часового пояса, а просто способ посчитать время
 * без учёта поясов и перехода на летнее время.
 */
internal fun LocalDateTime.toChartX(): Double = toEpochSecond(ZoneOffset.UTC) / SECONDS_PER_DAY

internal fun Double.toChartDate(): LocalDate = LocalDate.ofEpochDay(roundToLong())

/**
 * Расширяет диапазон X до целых суток: от полуночи первого дня до полуночи после последней точки.
 * Диапазон Y берётся из стандартного auto().
 */
/** Диапазон X — целые сутки (метки в 00:00), диапазон Y берётся из [yRange]. */
internal class WholeDaysRangeProvider(
    yRange: CartesianLayerRangeProvider,
) : CartesianLayerRangeProvider by yRange {
    override fun getMinX(minX: Double, maxX: Double, extraStore: ExtraStore) = floor(minX)
    override fun getMaxX(minX: Double, maxX: Double, extraStore: ExtraStore) = ceil(maxX)
}

/** Запас по Y вокруг данных, чтобы почти постоянная линия не лежала на краю графика. */
internal class PaddedYRange(private val padding: Double) : CartesianLayerRangeProvider {
    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) =
        floor(minY - padding)

    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) =
        ceil(maxY + padding)
}