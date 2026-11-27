package ru.vik.spindel.core.network.fake

import jakarta.inject.Inject
import kotlinx.coroutines.delay
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.devices.api.Device
import ru.vik.spindel.core.metrics.api.Metric
import ru.vik.spindel.core.metrics.testdata.generateFermentationMetrics
import ru.vik.spindel.core.metrics.testdata.generateTrashMetrics
import ru.vik.spindel.core.network.api.NetworkRepository
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class FakeNetworkRepository @Inject constructor() : NetworkRepository {
    private var brewKey: Long = 0
    private var deviceKey: Long = 0
    // Счетчик запросов списка варки, чтобы можно было возвращаться данные не при запуске программы, а чуть позже
    private var brewListRefreshCounter: Long = 0
    private val brewList: MutableList<Brew> = mutableListOf(
    )
    private val deviceList: List<Device> = listOf(
        Device(++deviceKey, "Второй"),
    )

    override fun getBrew(brewId: Long): Result<Brew> {
        val brew = brewList.find { it.id == brewId }
        return if (brew != null)
            Result.success(brew)
        else
            Result.failure(Exception("Не найдено"))
    }

    override suspend fun getBrewList(): Result<List<Brew>> {
        simulateNetDelay()

        // Случайная ошибка
        if (Random.nextInt(10) == 0)
            return Result.failure(Exception("Some random error"))

        // Имитация ошибка сети, чтобы загрузка данных проходила не с первого раза.
        if (brewListRefreshCounter++ == 0L) {
            brewList.addAll(
                listOf(
                    Brew(++brewKey, "Рождественский грог", LocalDateTime.of(2026, 1, 7, 12, 0, 0), false),
                    Brew(++brewKey, "Стаут", LocalDateTime.of(2026, 1, 28, 12, 0, 0), false),
                    Brew(++brewKey, "Milkshake", LocalDateTime.of(2026, 2, 14, 12, 0, 0), false),
                    Brew(++brewKey, "Первомайская варка", LocalDateTime.of(2026, 5, 1, 12, 0, 0), false),
                    Brew(++brewKey, "Майская варка", LocalDateTime.of(2026, 5, 20, 12, 0, 0), false),
                    Brew(++brewKey, "Быстролагер", LocalDateTime.of(2026, 6, 3, 12, 0, 0), false),
                    Brew(++brewKey, "Светлый эль", LocalDateTime.of(2026, 7, 1, 14, 0, 0), true),
                    Brew(++brewKey, "IPA осенний", LocalDateTime.of(2026, 9, 18, 14, 0, 0), true),
                    Brew(++brewKey, "Тыквенный эль", LocalDateTime.of(2026, 10, 1, 14, 0, 0), true),
                )
            )
        }
        return Result.success(brewList)
    }

    override suspend fun getMetricList(brewId: Long?): Result<List<Metric>> {
        simulateNetDelay()
        val brew = brewList.find { brew -> brew.id == brewId }
        val startBrewTime = brew?.date ?: LocalDateTime.now()
        val list = if (brewId == null) {
            generateTrashMetrics(startBrewTime, Random(-1))
        } else {
            generateFermentationMetrics(startBrewTime, Random(brewId))
        }

        val baseIndex = if (brewId == null) 0 else (brewId + 1) * 1_000_000
        list.mapIndexed { index, metric ->
            metric.copy(id = baseIndex + index)
        }

        return Result.success(list)
    }

    override fun getDevice(deviceId: Long): Result<Device> {
        val device = deviceList.find { it.id == deviceId }
        return if (device != null)
            Result.success(device)
        else
            Result.failure(Exception("Не найдено"))
    }

    override fun getDeviceList(): Result<List<Device>> {
        return Result.success(deviceList)
    }

    /** Имитация внутренних задержек при работе с сетью. **/
    private suspend fun simulateNetDelay() = delay(1500.milliseconds)
}