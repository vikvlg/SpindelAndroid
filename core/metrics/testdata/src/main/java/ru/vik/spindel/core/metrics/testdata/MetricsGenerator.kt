package ru.vik.spindel.core.metrics.testdata

import ru.vik.spindel.core.metrics.api.Metric
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random

// --- Настройки температуры (подбираются вручную) ---

/**
 * Скорость, с которой температура сусла стремится к целевой, ч.
 * За это время температура проходит ~63% пути до целевой, за втрое большее — ~95%.
 * Меньше значение — быстрее реакция.
 */
private const val TEMPERATURE_RESPONSE_HOURS = 16.0

/** Границы случайного повышения целевой температуры во время активного брожения, °C. */
private const val ACTIVE_PHASE_TEMPERATURE_RISE_MIN = 1.0
private const val ACTIVE_PHASE_TEMPERATURE_RISE_MAX = 2.0

/** Максимальное отклонение температуры от целевой из-за плавных колебаний, °C. */
private const val TEMPERATURE_WOBBLE_MAX = 0.5

/** Типичная амплитуда плавных колебаний температуры, °C. */
private const val TEMPERATURE_WOBBLE_STD = 0.2

/** Насколько колебания в соседние часы похожи друг на друга; ближе к 1 — плавнее и медленнее. */
private const val TEMPERATURE_WOBBLE_CORRELATION = 0.97

fun generateFermentationMetrics(
    startTime: LocalDateTime,
    random: Random = Random.Default,
): List<Metric> {
    val originalGravity = random.nextDouble(10.0, 16.0)
    val attenuationPercent = random.nextDouble(70.0, 90.0)
    val durationDays = random.nextInt(10, 21)
    val startTemperature = random.nextDouble(22.0, 30.0)
    val targetTemperature = random.nextDouble(10.0, 18.0)

    return generateFermentationMetrics(
        startTime,
        originalGravity,
        attenuationPercent,
        durationDays,
        startTemperature,
        targetTemperature,
        random)
}

/**
 * Генерирует тестовые измерения плотности сусла (°P) и температуры (°C) во время брожения.
 *
 * Плотность: лаг-фаза 12–24 ч, когда плотность почти не меняется → плавный разгон
 * и быстрое падение в первые дни → медленное линейное снижение до конечной плотности
 * к концу срока. Поверх кривой накладывается погрешность прибора: плавный дрейф
 * и мелкий шум, в сумме не больше ±0.2 °P.
 *
 * Температура: начинается со [startTemperature] и плавно стремится к целевой. Целевая равна
 * [targetTemperature], на время активного брожения повышается на 1–2°, а когда плотность
 * переходит в линейную фазу, возвращается обратно. Поверх идут очень плавные колебания
 * до ±0.5°.
 *
 * @param startTime начало брожения, время первой точки
 * @param originalGravity начальная плотность, °P (10–16)
 * @param attenuationPercent кажущаяся степень сбраживания, % (70–90)
 * @param durationDays продолжительность брожения, сутки (10–21)
 * @param startTemperature температура сусла в начале брожения, °C (20–30)
 * @param targetTemperature целевая температура брожения, °C (10–20)
 * @param random источник случайных чисел; с фиксированным seed результат воспроизводим
 */
fun generateFermentationMetrics(
    startTime: LocalDateTime,
    originalGravity: Double,
    attenuationPercent: Double,
    durationDays: Int,
    startTemperature: Double,
    targetTemperature: Double,
    random: Random = Random.Default,
): List<Metric> {
    require(originalGravity in 10.0..16.0) {
        "Начальная плотность должна быть 10–16 °P, получено $originalGravity"
    }
    require(attenuationPercent in 70.0..90.0) {
        "Степень сбраживания должна быть 70–90 %, получено $attenuationPercent"
    }
    require(durationDays in 10..21) {
        "Продолжительность должна быть 10–21 сутки, получено $durationDays"
    }
    require(startTemperature in 20.0..30.0) {
        "Начальная температура должна быть 20–30 °C, получено $startTemperature"
    }
    require(targetTemperature in 10.0..20.0) {
        "Целевая температура должна быть 10–20 °C, получено $targetTemperature"
    }

    val finalGravity = originalGravity * (1 - attenuationPercent / 100)

    // --- Параметры кривой, случайные для каждой варки ---
    val lagHours = random.nextDouble(12.0, 24.0)
    val activeHours = durationDays * 24.0 - lagHours // от конца лаг-фазы до конца брожения
    // Темп активной фазы, ч: пик скорости примерно через fastTau часов после лага,
    // около 95% быстрого падения проходит за 3.5–5 суток
    val fastTau = random.nextDouble(18.0, 26.0)
    // Какая доля всего падения плотности приходится на активную фазу (остальное даёт линейный «хвост»)
    val fastShare = random.nextDouble(0.82, 0.90)

    // Распределение Эрланга второго порядка: скорость после лага нарастает плавно,
    // а не скачком, затем падение замедляется
    fun fastProgress(h: Double): Double = 1 - (1 + h / fastTau) * exp(-h / fastTau)
    val fastProgressAtEnd = fastProgress(activeHours)

    fun idealGravity(hoursFromStart: Double): Double {
        val h = hoursFromStart - lagHours
        if (h <= 0) return originalGravity
        val progress = fastShare * fastProgress(h) / fastProgressAtEnd +
                (1 - fastShare) * h / activeHours
        return originalGravity - (originalGravity - finalGravity) * progress.coerceAtMost(1.0)
    }

    // --- Погрешность прибора: медленный дрейф (процесс AR(1)) + мелкий независимый шум ---
    val driftCorrelation = 0.92 // насколько дрейф в соседние часы похож; ближе к 1 — плавнее
    val driftStd = 0.07         // °P, типичная амплитуда дрейфа
    val driftStep = driftStd * sqrt(1 - driftCorrelation * driftCorrelation)
    val jitterStd = 0.03        // °P, мелкий шум в каждой точке
    val maxError = 0.2          // °P, жёсткая граница суммарной погрешности
    var drift = random.nextGaussian() * driftStd

    // --- Температура ---
    // Активная фаза длится от конца лага до момента, когда пройдено ~95% быстрого падения
    // плотности (для распределения Эрланга второго порядка это 4.74 × fastTau), дальше идёт
    // линейный «хвост»
    val activePhaseEndHours = lagHours + 4.74 * fastTau
    val activePhaseTemperatureRise =
        random.nextDouble(ACTIVE_PHASE_TEMPERATURE_RISE_MIN, ACTIVE_PHASE_TEMPERATURE_RISE_MAX)

    fun baseTargetTemperature(hoursFromStart: Double): Double =
        if (hoursFromStart > lagHours && hoursFromStart < activePhaseEndHours) {
            targetTemperature + activePhaseTemperatureRise
        } else {
            targetTemperature
        }

    val wobbleStep = TEMPERATURE_WOBBLE_STD *
            sqrt(1 - TEMPERATURE_WOBBLE_CORRELATION * TEMPERATURE_WOBBLE_CORRELATION)
    var temperature = startTemperature
    var temperatureWobble = 0.0 // колебания нарастают с нуля, первая точка ровно startTemperature

    val endTime = startTime.plusDays(durationDays.toLong())

    return buildList {
        var time = startTime
        while (!time.isAfter(endTime)) {
            val hoursFromStart = Duration.between(startTime, time).toMillis() / 3_600_000.0
            val error = (drift + random.nextGaussian() * jitterStd).coerceIn(-maxError, maxError)

            add(
                Metric(
                    id = size + 1L,
                    date = time,
                    gravity = (idealGravity(hoursFromStart) + error).roundTo2(),
                    temperature = temperature.roundTo2(),
                )
            )

            // Следующая точка через час ± минута от предыдущей, сдвиг накапливается
            val stepSeconds = 3600 + random.nextLong(-60, 61)
            val stepHours = stepSeconds / 3600.0

            drift = drift * driftCorrelation + random.nextGaussian() * driftStep

            // Колебания сдвигают целевую температуру, а сама температура догоняет цель
            // по экспоненте, поэтому колебания получаются плавными
            temperatureWobble = (
                    temperatureWobble * TEMPERATURE_WOBBLE_CORRELATION +
                            random.nextGaussian() * wobbleStep
                    ).coerceIn(-TEMPERATURE_WOBBLE_MAX, TEMPERATURE_WOBBLE_MAX)
            val target = baseTargetTemperature(hoursFromStart) + temperatureWobble
            temperature += (target - temperature) * (1 - exp(-stepHours / TEMPERATURE_RESPONSE_HOURS))

            time = time.plusSeconds(stepSeconds)
        }
    }
}

fun generateTrashMetrics(
    startTime: LocalDateTime = LocalDateTime.now(),
    random: Random = Random.Default,
): List<Metric> {
    val count = random.nextInt(3, 10)
    var time = startTime.minusDays(7)
    return buildList {
        for (i in 0..<count) {
            add(
                Metric(
                    id = i + 1L,
                    date = time,
                    gravity = random.nextDouble(5.0, 25.0),
                    temperature = random.nextDouble(10.0, 45.0),
                )
            )
            time = time.plusMinutes(random.nextLong(55, 65))
        }
    }
}

/** Нормально распределённое случайное число (среднее 0, σ = 1), метод Бокса — Мюллера. */
private fun Random.nextGaussian(): Double {
    val u1 = 1.0 - nextDouble() // (0, 1], чтобы не получить ln(0)
    val u2 = nextDouble()
    return sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
}

/** Округление до сотых, как показывают бытовые плотномеры. */
private fun Double.roundTo2(): Double = round(this * 100) / 100
