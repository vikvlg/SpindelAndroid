package ru.vik.spindel.core.database.fake

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

class MetricsGenerator {
    /**
    • Генерирует тестовые измерения плотности сусла (°P) во время брожения.
     *
    • Форма кривой: лаг-фаза 12–24 ч, когда плотность почти не меняется → плавный разгон
    • и быстрое падение в первые дни → медленное линейное снижение до конечной плотности
    • к концу срока. Поверх кривой накладывается погрешность прибора: плавный дрейф
    • и мелкий шум, в сумме не больше ±0.2 °P.
     *
    • @param startTime начало брожения, время первой точки
    • @param originalGravity начальная плотность, °P (10–16)
    • @param attenuationPercent кажущаяся степень сбраживания, % (70–90)
    • @param durationDays продолжительность брожения, сутки (10–21)
    • @param random источник случайных чисел; с фиксированным seed результат воспроизводим
     */
    fun generateFermentationMetrics(
        startTime: LocalDateTime,
        originalGravity: Double,
        attenuationPercent: Double,
        durationDays: Int,
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
                        temperature = 16.0,
                    )
                )

                drift = drift * driftCorrelation + random.nextGaussian() * driftStep
                // Следующая точка через час ± минута от предыдущей, сдвиг накапливается
                time = time.plusSeconds(3600 + random.nextLong(-60, 61))
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
}
